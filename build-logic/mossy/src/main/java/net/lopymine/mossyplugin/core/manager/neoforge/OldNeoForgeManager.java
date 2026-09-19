package net.lopymine.mossyplugin.core.manager.neoforge;

import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mossyplugin.common.MossyUtils;
import net.lopymine.mossyplugin.core.MossyPluginCore;
import net.lopymine.mossyplugin.core.data.MossyProjectConfigurationData;
import net.lopymine.mossyplugin.core.extension.MossyCoreDependenciesExtension;
import net.lopymine.mossyplugin.core.util.TestRuns;
import net.neoforged.gradle.dsl.common.extensions.Minecraft;
import net.neoforged.gradle.dsl.common.extensions.subsystems.*;
import net.neoforged.gradle.dsl.common.runs.run.*;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.jetbrains.annotations.NotNull;

@ExtensionMethod(MossyPluginCore.class)
public class OldNeoForgeManager {

	public static void apply(@NotNull MossyProjectConfigurationData data, MossyCoreDependenciesExtension dependencies) {
		Project project = data.project();

		Properties personalProperties = project.getPersonalProperties();

		String playerNickname = MossyUtils.getPlayerNickname(personalProperties);
		Map<String, UUID> altAccounts = MossyUtils.getAltAccounts(personalProperties);
		UUID playerUuid = MossyUtils.getPlayerUuid(personalProperties);
		Object quickPlayWorld = personalProperties.get("quick_play_world");

		Minecraft minecraft = project.getExtensions().getByType(Minecraft.class);
		minecraft.getAccessTransformers().getFiles().from("../../src/main/resources/aws/%s-%s.cfg".formatted(data.loaderName(), data.minecraftVersion()));

		Parchment parchment = project.getExtensions().getByType(Subsystems.class).getParchment();
		parchment.getMinecraftVersion().set(dependencies.getMinecraft());
		parchment.getMappingsVersion().set(dependencies.getParchment());

		String sides = project.getProperty("data.sides").toLowerCase(Locale.ROOT);
		boolean createClient = sides.equals("client") || sides.equals("both");
		boolean createServer = sides.equals("server") || sides.equals("both");

		SourceSet main = project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().getByName("main");
		RunManager container = project.getExtensions().getByType(RunManager.class);

		Path runs = project.getRootProject().getProjectDir().toPath().resolve("runs");

		if (createClient) {
			Run client = container.create("client");
			client.getWorkingDirectory().set(runs.resolve("client").toFile());

			addProgramArg(client, "--username", playerNickname);
			addProgramArg(client, "--uuid", playerUuid);
			addProgramArg(client, "--quickPlaySingleplayer", quickPlayWorld);

			for (Entry<String, UUID> entry : altAccounts.entrySet()) {
				String runName = "client_" + entry.getKey();

				Run altClient = container.create(runName);
				altClient.runType("client");
				altClient.getWorkingDirectory().set(runs.resolve(runName).toFile());

				addProgramArg(altClient, "--username", entry.getKey());
				addProgramArg(altClient, "--uuid", entry.getValue());
				addProgramArg(altClient, "--quickPlaySingleplayer", quickPlayWorld);
			}
		}

		if (createServer) {
			Run server = container.create("server");
			server.getWorkingDirectory().set(runs.resolve("server").toFile());
			server.getArguments().add("--nogui");
		}

		container.configureEach((run) -> {
			run.modSource(main);
		});

		if (!TestRuns.isEnabled(project)) {
			return;
		}

		if (createClient) {
			Run clientTest = container.create("clientTest");
			clientTest.runType("client");
			clientTest.getWorkingDirectory().set(TestRuns.getRunDirectory(project, "client"));
			clientTest.getModSources().add(main);

			addProgramArg(clientTest, "--username", playerNickname);
			addProgramArg(clientTest, "--uuid", playerUuid);
			addProgramArg(clientTest, "--quickPlaySingleplayer", quickPlayWorld);

			for (Entry<String, UUID> entry : altAccounts.entrySet()) {
				Run altClientTest = container.create("clientTest_%s".formatted(entry.getKey()));
				altClientTest.runType("client");
				altClientTest.getWorkingDirectory().set(TestRuns.getRunDirectory(project, "client_%s".formatted(entry.getKey())));
				altClientTest.getModSources().add(main);

				addProgramArg(altClientTest, "--username", entry.getKey());
				addProgramArg(altClientTest, "--uuid", entry.getValue());
				addProgramArg(altClientTest, "--quickPlaySingleplayer", quickPlayWorld);
			}
		}

		if (createServer) {
			Run serverTest = container.create("serverTest");
			serverTest.runType("server");
			serverTest.getWorkingDirectory().set(TestRuns.getRunDirectory(project, "server"));
			serverTest.getModSources().add(main);
			serverTest.getArguments().add("--nogui");
		}
	}

	private static void addProgramArg(Run run, String key, Object argument) {
		if (argument == null || argument.toString().equals("none")) {
			return;
		}
		run.getArguments().add(key);
		run.getArguments().add(argument.toString());
	}

}
