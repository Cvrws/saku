package cc.unknown.module.api;

import org.lwjgl.input.Keyboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {
    String[] aliases();

    String description();

    Category category();

    int keyBind() default Keyboard.KEY_NONE;

    boolean autoEnabled() default false;

    boolean allowDisable() default true;
}