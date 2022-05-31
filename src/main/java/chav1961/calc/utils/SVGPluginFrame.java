package chav1961.calc.utils;


import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Locale;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.interfaces.SVGIconKeeper;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.growablearrays.GrowableCharArray;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.SwingUtils;

public class SVGPluginFrame<T> extends InnerFrame<T> {
	private static final long 		serialVersionUID = 1L;
	
    private final Localizer					localizer;
    private final AutoBuiltForm<T,?>		abf;
    private final InnerSVGPluginWindow<T>	w;
    
    public SVGPluginFrame(final Localizer localizer, final Class<T> instanceClass, final T instance) throws ContentException {
        super(instanceClass);
        
        if (instanceClass == null) {
        	throw new NullPointerException("Instance class to show can't be null"); 
        }
        else if (instance == null) {
        	throw new NullPointerException("Instance to show can't be null"); 
        }
        else if (!(instance instanceof FormManager<?,?>)) {
        	throw new IllegalArgumentException("Instance passed must implements "+FormManager.class.getCanonicalName()+" interface!"); 
        }
        else if (!instanceClass.isAnnotationPresent(PluginProperties.class)) {
        	throw new IllegalArgumentException("Instance passed must be annotated with @"+PluginProperties.class.getCanonicalName()); 
        }
        else {
        	final PluginProperties	pp = instanceClass.getAnnotation(PluginProperties.class);

        	this.localizer = localizer;
        	
			try{final FormManager<Object,T>	wrapper = new FormManagerWrapper<>((FormManager<Object,T>)instance, ()-> {refresh();},(name)->{selectIcon(name);}); 
				
				abf = new AutoBuiltForm<T,Object>(ContentModelFactory.forAnnotatedClass(instanceClass),localizer,PureLibSettings.INTERNAL_LOADER,instance, wrapper);
				
				for (Module m : abf.getUnnamedModules()) {
					instanceClass.getModule().addExports(instanceClass.getPackageName(),m);
				}
				w = new InnerSVGPluginWindow<T>(instanceClass,pp.svgURI(),abf,(src)->{
					try{final Field		field = instanceClass.getField(src); 
						final Object 	result = field.get(instance);
						
						if (result instanceof Enum<?>) {
							final Class<?>	enumClass = result.getClass();
							final Field		enumField = enumClass.getField(((Enum<?>)result).name()); 
						
							if (enumClass.isAnnotationPresent(LocaleResourceLocation.class) && enumField.isAnnotationPresent(LocaleResource.class)) {
								final LocaleResource	res = enumField.getAnnotation(LocaleResource.class);
								final Localizer			loc = LocalizerFactory.getLocalizer(URI.create(enumClass.getAnnotation(LocaleResourceLocation.class).value())); 
							
								return loc.getValue(res.value());
							}
							else {
								return result.toString();
							}
						}
						else if (field.isAnnotationPresent(Format.class)) {
							final FieldFormat	ff = new FieldFormat(field.getType(),field.getAnnotation(Format.class).value());
							
							return result == null ? "" : ff.print(result,FieldFormat.PrintMode.SINGLE_TEXT);
						}
						else {
							return result == null ? "" : result.toString();
						}
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | SyntaxException | LocalizationException e1) {
						return "<"+src+">";
					}
				});

				SwingUtils.assignActionKey(w,SwingUtils.KS_HELP,(e)->{showHelp(e.getActionCommand());},abf.getContentModel().getRoot().getHelpId());
				SwingUtils.assignActionKey(w,SwingUtils.KS_CLOSE,(e)->{try{setClosed(true);} catch (PropertyVetoException exc) {}},"close");
	        	fillLocalizedStrings(localizer.currentLocale().getLocale(),localizer.currentLocale().getLocale());

	        	getContentPane().add(w);
	        	
				if (pp.leftWidth() == -1) {
					final Dimension		viewSize = w.getInnerSVGSize(), preferences;
					
					if (viewSize.width > viewSize.height) {
						preferences = new Dimension(pp.width() - pp.height() * viewSize.height / viewSize.width, pp.height());
					}
					else {
						preferences = new Dimension(pp.width() - pp.height() * viewSize.width / viewSize.height, pp.height());
					}
					abf.setPreferredSize(preferences.width > pp.width()/2 ? new Dimension(pp.width()/2,pp.height()) : preferences);
				}
				else {
					abf.setPreferredSize(new Dimension(pp.width() - pp.leftWidth(),pp.height()));
				}
			} catch (IllegalArgumentException | LocalizationException | NullPointerException |  IOException exc) {
				throw new ContentException(exc);
			}
        }
    }

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale,newLocale);
	}

	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException, IllegalArgumentException {
		setTitle("#"+getWindowId()+": "+abf.getLocalizerAssociated().getValue(abf.getContentModel().getRoot().getLabelId()));
		w.localeChanged(oldLocale, newLocale);
	}

	private void showHelp(final String helpId) {
		try{SwingUtils.showCreoleHelpWindow(this,URIUtils.convert2selfURI(Utils.fromResource(localizer.getContent(helpId)).toCharArray(),"UTF-8"));
		} catch (LocalizationException | NullPointerException | IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void refresh() {
		w.refresh();
	}

	private void selectIcon(final String icon) {
		w.selectIcon(icon);
	}
	
	private static class FormManagerWrapper<T> implements FormManager<Object, T> {
		@FunctionalInterface
		private interface Refresher {
			void refresh();
		}		

		@FunctionalInterface
		private interface Iconizer {
			void setIcon(String icon);
		}		
		
		private final FormManager<Object, T>	delegate;
		private final Refresher					refresher;
		private final Iconizer					iconizer;
		
		private FormManagerWrapper(final FormManager<Object, T> delegate, final Refresher refresher, final Iconizer iconizer) {
			this.delegate = delegate;
			this.refresher = refresher;
			this.iconizer = iconizer;
		}

		@Override
		public RefreshMode onField(T inst, Object id, String fieldName, Object oldValue, boolean beforeCommit) throws FlowException, LocalizationException {
			final RefreshMode	mode = delegate.onField(inst, id, fieldName, oldValue, beforeCommit);

			if (oldValue instanceof SVGIconKeeper) {
				try{iconizer.setIcon(((SVGIconKeeper)inst.getClass().getField(fieldName).get(inst)).getSVGIcon());
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				}
			}
			if (mode != RefreshMode.NONE && mode != RefreshMode.REJECT) {
				refresher.refresh();
			}
			return mode;
		}

		@Override
		public RefreshMode onAction(final T inst, final Object id, final String actionName, final Object... parameter) throws FlowException, LocalizationException {
			final RefreshMode	mode = delegate.onAction(inst, id, actionName, parameter);
			
			if (mode != RefreshMode.NONE && mode != RefreshMode.REJECT) {
				refresher.refresh();
			}
			return mode;
		}
		
		@Override
		public LoggerFacade getLogger() {
			return delegate.getLogger();
		}
	}
}