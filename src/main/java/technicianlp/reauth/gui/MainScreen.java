package technicianlp.reauth.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.VersionChecker;
import org.apache.maven.artifact.versioning.ComparableVersion;
import technicianlp.reauth.ReAuth;
import technicianlp.reauth.authentication.flows.Flows;
import technicianlp.reauth.configuration.Profile;

import java.util.Map;

/**
 * Selection screen for login method
 * <p>
 * Login with Xbox Live:
 * [Use This device] [Use other device]
 * Login with Mojang:
 * [login]
 * Play Offline:
 * [Choose Username]
 * <p>
 * - has back button (to multiplayer/prev screen)
 */
public final class MainScreen extends AbstractScreen {

    private String message = null;

    public MainScreen() {
        super("reauth.gui.title.main");
    }

    @Override
    public void init() {
        super.init();

        int buttonWidthH = BUTTON_WIDTH / 2;
        int y = this.centerY - 55;

        SaveButton.ITooltip saveButtonTooltip = (button, matrixStack, mouseX, mouseY) -> this.renderTooltip(matrixStack, this.font.split(new TranslatableComponent("reauth.gui.button.save.tooltip"), 250), mouseX, mouseY);
        SaveButton saveButton = new SaveButton(this.centerX - buttonWidthH, y + 70, new TranslatableComponent("reauth.gui.button.save"), saveButtonTooltip);
        this.addRenderableWidget(saveButton);

        Profile profile = ReAuth.profiles.getProfile();
        if (profile != null) {
            Component text = new TranslatableComponent("reauth.gui.profile", profile.getValue(Profile.NAME, "Steve"));
            this.addRenderableWidget(new Button(this.centerX - buttonWidthH, y + 10, BUTTON_WIDTH, 20, text, (b) -> {
                FlowScreen screen = new FlowScreen();
                screen.setFlow(Flows.loginWithProfile(profile, screen));
                this.transitionScreen(screen);
            }));
        } else {
            Button profileButton = new Button(this.centerX - buttonWidthH, y + 10, BUTTON_WIDTH, 20, new TranslatableComponent("reauth.gui.noProfile"), (b) -> {
            });
            profileButton.active = false;
            this.addRenderableWidget(profileButton);
        }

        this.addRenderableWidget(new Button(this.centerX - buttonWidthH, y + 45, buttonWidthH - 1, 20, new TranslatableComponent("This Device"), (b) -> {
            FlowScreen screen = new FlowScreen();
            screen.setFlow(Flows.loginWithAuthCode(saveButton.selected(), screen));
            this.transitionScreen(screen);
        }));
        this.addRenderableWidget(new Button(this.centerX + 1, y + 45, buttonWidthH - 1, 20, new TranslatableComponent("Any Device"), (b) -> {
            FlowScreen screen = new FlowScreen();
            screen.setFlow(Flows.loginWithDeviceCode(saveButton.selected(), screen));
            this.transitionScreen(screen);
        }));
        this.addRenderableWidget(new Button(this.centerX - buttonWidthH, y + 105, BUTTON_WIDTH, 20, new TranslatableComponent("Choose Username"), (b) -> this.transitionScreen(new OfflineLoginScreen())));


        VersionChecker.CheckResult result = VersionChecker.getResult(ReAuth.modInfo);
        if (result.status() == VersionChecker.Status.OUTDATED) {
            // Cannot be null but is marked as such :(
            Map<ComparableVersion, String> changes = result.changes();
            if (changes != null) {
                String msg = changes.get(result.target());
                if (msg != null) {
                    this.message = I18n.get("reauth.gui.auth.update", msg);
                }
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);

        int x = this.centerX - BUTTON_WIDTH / 2;
        this.font.drawShadow(poseStack, I18n.get("reauth.gui.text.profile"), x, this.centerY - 55, 0xFFFFFFFF);
        this.font.drawShadow(poseStack, I18n.get("reauth.gui.text.microsoft"), x, this.centerY - 20, 0xFFFFFFFF);
        this.font.drawShadow(poseStack, I18n.get("reauth.gui.text.offline"), x, this.centerY + 40, 0xFFFFFFFF);

        if (this.message != null) {
            this.font.drawShadow(poseStack, this.message, x, this.baseY + 20, 0xFFFFFFFF);
        }
    }

    @Override
    protected void requestClose(boolean completely) {
        super.requestClose(true);
    }
}
