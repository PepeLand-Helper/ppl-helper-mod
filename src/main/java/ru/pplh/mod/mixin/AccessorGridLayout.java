package ru.pplh.mod.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(GridLayout.class)
public interface AccessorGridLayout {
    @Accessor
    List<AbstractWidget> getChildren();
}
