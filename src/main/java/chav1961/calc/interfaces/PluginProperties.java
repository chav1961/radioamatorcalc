package chav1961.calc.interfaces;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ TYPE})
public @interface PluginProperties {
	int width();
	int height();
	int leftWidth() default -1;
	String svgURI();
	String pluginIconURI() default "";
	String desktopIconURI() default "";
	boolean resizable() default true;
}
