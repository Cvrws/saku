package cc.unknown.ui.click.screen;

public interface Screen {

    default void onDraw(final int mouseX, final int mouseY, final float partialTicks) {

    }

    default void onKey(final char typedChar, final int keyCode) {

    }

    default void onClick(final int mouseX, final int mouseY, final int mouseButton) {

    }

    default void onMouseRelease() {

    }

    default void onInit() {

    }

    default boolean automaticSearchSwitching() {
        return true;
    }

    default boolean hideSideBar() {
        return false;
    }
}
