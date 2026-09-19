package net.lopymine.patpat.entrypoint.impl.forge;

//? if forge {

/*import net.lopymine.patpat.PatPat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(PatPat.MOD_ID)
public class ForgeCommonEntrypoint {

	private static net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext context;

	//? if >=1.20.1 {
	public ForgeCommonEntrypoint(net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext loadingContext) {
		context = loadingContext;
	//?} else {
	/^public ForgeCommonEntrypoint() {
		context = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get();
	^///?}
		PatPat.onInitialize();

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ForgeClientEntrypoint::onInitializeClient);
		DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> ForgeDedicatedEntrypoint::onInitializeServer);
	}

	public static net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext getContext() {
		return context;
	}

}
*///?}
