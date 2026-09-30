package fr.iglee42.createcasing.client;

import com.zurrtum.create.client.catnip.gui.AbstractSimiScreen;
import com.zurrtum.create.client.catnip.gui.element.GuiGameElement;
import com.zurrtum.create.client.catnip.gui.widget.ElementWidget;
import com.zurrtum.create.client.foundation.gui.AllIcons;
import com.zurrtum.create.client.foundation.gui.widget.IconButton;
import com.zurrtum.create.client.foundation.gui.widget.Label;
import com.zurrtum.create.client.foundation.gui.widget.ScrollInput;
import com.zurrtum.create.client.foundation.gui.widget.SelectionScrollInput;
import fr.iglee42.createcasing.CreateCasing;
import fr.iglee42.createcasing.blockEntities.AutoClutchBlockEntity;
import fr.iglee42.createcasing.packets.ConfigureAutoClutchPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/** Mode, comparison and value of an automatic clutch. Sends its settings when closed, as upstream. */
public class AutoClutchScreen extends AbstractSimiScreen {
    private static final Identifier BACKGROUND = CreateCasing.asResource("textures/gui/automatic_clutch.png");
    private static final int WIDTH = 254;
    private static final int HEIGHT = 104;

    protected final AutoClutchBlockEntity be;
    protected ScrollInput maxStressWidget;
    private SelectionScrollInput scrollInput;
    private SelectionScrollInput opsInput;

    public AutoClutchScreen(AutoClutchBlockEntity be) {
        super(be.getBlockState().getBlock().getName());
        this.be = be;
    }

    @Override
    protected void init() {
        setWindowSize(WIDTH, HEIGHT);
        super.init();
        clearWidgets();

        int x = guiLeft;
        int y = guiTop;
        ItemStack clutch = new ItemStack(be.getBlockState().getBlock());

        addRenderableWidget(new ElementWidget(x + 51, y + 48).showingElement(GuiGameElement.of(clutch)));

        scrollInput = new SelectionScrollInput(x + 51, y + 25, 145, 18);
        Label scrollInputLabel = new Label(x + 53, y + 29, CommonComponents.EMPTY).withShadow();
        scrollInput.forOptions(AutoClutchBlockEntity.Mode.getComponents())
            .titled(Component.translatable(CreateCasing.MODID + ".auto_clutch.mode"))
            .writingTo(scrollInputLabel)
            .setState(be.getMode().ordinal());
        addRenderableWidget(scrollInputLabel);
        addRenderableWidget(scrollInput);

        Label valueLabel = new Label(x + 105, y + 52, Component.empty()).withShadow();
        maxStressWidget = new ScrollInput(x + 104, y + 48, 91, 18);
        maxStressWidget.withRange(0, Integer.MAX_VALUE)
            .writingTo(valueLabel)
            .withStepFunction(context -> context.control ? (context.shift ? 1024 : 512) : context.shift ? 128 : 1)
            .titled(Component.translatable("createcasing.auto_clutch.configured_value"))
            .format(i -> Component.literal(addSpacesEveryThreeDigits(i)));
        maxStressWidget.setState(be.getConfiguredValue());
        maxStressWidget.onChanged();
        addRenderableWidget(valueLabel);
        addRenderableWidget(maxStressWidget);

        IconButton confirmButton = new IconButton(x + WIDTH - 33, y + HEIGHT - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onClose);
        addRenderableWidget(confirmButton);

        opsInput = new SelectionScrollInput(x + 75, y + 48, 24, 18);
        Label opsLabel = new Label(x + 80, y + 52, Component.empty()).withShadow();
        opsInput.forOptions(AutoClutchBlockEntity.Operation.getComponents())
            .setState(be.getOperation().ordinal())
            .titled(Component.translatable("createcasing.auto_clutch.operation"))
            .format(state -> Component.literal(" " + AutoClutchBlockEntity.Operation.values()[state].formatted))
            .writingTo(opsLabel)
            .calling(state -> send(state));
        addRenderableWidget(opsLabel);
        addRenderableWidget(opsInput);

        addRenderableWidget(new ElementWidget(x + WIDTH, y + HEIGHT - 56).showingElement(GuiGameElement.of(clutch).scale(5)));
    }

    private static String addSpacesEveryThreeDigits(int number) {
        String numberStr = String.valueOf(number);
        StringBuilder formatted = new StringBuilder();
        int count = 0;
        for (int i = numberStr.length() - 1; i >= 0; i--) {
            formatted.insert(0, numberStr.charAt(i));
            count++;
            if (count % 3 == 0 && i > 0)
                formatted.insert(0, " ");
        }
        return formatted.toString();
    }

    @Override
    protected void renderWindow(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, 0, 0, WIDTH, HEIGHT, 256, 256);
        graphics.text(font, title, x + (WIDTH - 8) / 2 - font.width(title) / 2, y + 4, 0xFF592424, false);
    }

    private void send(int operation) {
        ClientPlayNetworking.send(new ConfigureAutoClutchPacket(be.getBlockPos(), maxStressWidget.getState(), scrollInput.getState(), operation));
    }

    @Override
    public void removed() {
        send(opsInput.getState());
    }
}
