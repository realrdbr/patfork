package net.lopymine.patpat.client.render.feature;

import com.mojang.blaze3d.vertex.*;
import java.util.*;
import lombok.*;
import lombok.experimental.ExtensionMethod;
import net.lopymine.patpat.compat.iris.IrisCompat;
import net.lopymine.patpat.extension.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

//? if >=26.3 {
import net.minecraft.client.renderer.SubmitNodeCollector;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.renderer.MultiBufferSource;
*///?}

//? if >=1.21.11 {
import net.minecraft.client.renderer.rendertype.RenderTypes;
//?} else {
/*import net.minecraft.client.renderer.RenderTypes;
 *///?}

@Setter
@Getter
@ExtensionMethod(value = {VertexConsumerExtension.class, PoseExtension.class})
public class PatFeatureRenderer {

	private boolean renderingLevel = false;

	//? if <26.3 {
	/*public final List<PatFeatureRequest> requests = new ArrayList<>();
	 *///?}

	private static final PatFeatureRenderer INSTANCE = new PatFeatureRenderer();

	public static PatFeatureRenderer getInstance() {
		return INSTANCE;
	}

	//? if >=26.3 {

	public void request(
			Identifier texture,
			PoseStack poseStack,
			float x1,
			float y1,
			float x2,
			float y2,
			float z,
			float u1,
			float v1,
			float u2,
			float v2,
			int light,
			@Nullable SubmitNodeCollector provider
	) {
		if (provider == null) {
			return;
		}

		provider.submitCustomGeometry(
				poseStack,
				RenderTypes.entityTranslucent(texture),
				(pose, buffer) -> {
					buffer.addVertex(pose, x1, y1, z)
							.setColor(255, 255, 255, 255)
							.setUv(u1, v1)
							.setOverlay(OverlayTexture.NO_OVERLAY)
							.setLight(light)
							.setNormal(pose, 0.0F, 1.0F, 0.0F);

					buffer.addVertex(pose, x1, y2, z)
							.setColor(255, 255, 255, 255)
							.setUv(u1, v2)
							.setOverlay(OverlayTexture.NO_OVERLAY)
							.setLight(light)
							.setNormal(pose, 0.0F, 1.0F, 0.0F);

					buffer.addVertex(pose, x2, y2, z)
							.setColor(255, 255, 255, 255)
							.setUv(u2, v2)
							.setOverlay(OverlayTexture.NO_OVERLAY)
							.setLight(light)
							.setNormal(pose, 0.0F, 1.0F, 0.0F);

					buffer.addVertex(pose, x2, y1, z)
							.setColor(255, 255, 255, 255)
							.setUv(u2, v1)
							.setOverlay(OverlayTexture.NO_OVERLAY)
							.setLight(light)
							.setNormal(pose, 0.0F, 1.0F, 0.0F);
				}
		);
	}

	//?} else {

        /*public void render(MultiBufferSource source) {
                if (!this.renderingLevel && !IrisCompat.isRenderingShadowPass()) {
                        return;
                }

                for (PatFeatureRequest request : this.requests) {
                        VertexConsumer buffer = (request.provider() == null ? source : request.provider())
                                        .getBuffer(RenderTypes.entityTranslucent(request.texture()));

                        org.joml.Matrix4f matrix = request.poseStack().pose();

                        buffer.withVertex(matrix, request.x1(), request.y1(), request.z())
                                        .withColor(255, 255, 255, 255)
                                        .withUv(request.u1(), request.v1())
                                        .withOverlay(OverlayTexture.NO_OVERLAY)
                                        .withLight(request.light())
                                        .withNormal(0, 1, 0)
                                        .end();

                        buffer.withVertex(matrix, request.x1(), request.y2(), request.z())
                                        .withColor(255, 255, 255, 255)
                                        .withUv(request.u1(), request.v2())
                                        .withOverlay(OverlayTexture.NO_OVERLAY)
                                        .withLight(request.light())
                                        .withNormal(0, 1, 0)
                                        .end();

                        buffer.withVertex(matrix, request.x2(), request.y2(), request.z())
                                        .withColor(255, 255, 255, 255)
                                        .withUv(request.u2(), request.v2())
                                        .withOverlay(OverlayTexture.NO_OVERLAY)
                                        .withLight(request.light())
                                        .withNormal(0, 1, 0)
                                        .end();

                        buffer.withVertex(matrix, request.x2(), request.y1(), request.z())
                                        .withColor(255, 255, 255, 255)
                                        .withUv(request.u2(), request.v1())
                                        .withOverlay(OverlayTexture.NO_OVERLAY)
                                        .withLight(request.light())
                                        .withNormal(0, 1, 0)
                                        .end();
                }

                this.requests.clear();
        }

        public void request(
                        Identifier texture,
                        Pose poseStack,
                        float x1,
                        float y1,
                        float x2,
                        float y2,
                        float z,
                        float u1,
                        float v1,
                        float u2,
                        float v2,
                        int light,
                        @Nullable MultiBufferSource provider
        ) {
                this.requests.add(
                                new PatFeatureRequest(
                                                texture,
                                                poseStack.copy(),
                                                x1, y1,
                                                x2, y2,
                                                z,
                                                u1, v1,
                                                u2, v2,
                                                light,
                                                provider
                                )
                );
        }
        *///?}
}