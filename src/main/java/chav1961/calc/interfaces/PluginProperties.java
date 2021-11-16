package chav1961.calc.interfaces;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ TYPE})
@Inherited
public @interface PluginProperties {
	int width();
	int height();
	boolean resizable() default true;
	String pluginIconURI() default "";
	String desktopIconURI() default "";
	int leftWidth() default -1;
	int topWidth() default -1;
	String svgURI() default "";
	int numberOfBars() default 1;
}
