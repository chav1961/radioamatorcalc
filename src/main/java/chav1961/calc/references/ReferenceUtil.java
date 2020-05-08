package chav1961.calc.references;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import chav1961.calc.interfaces.ReferenceColumn;
import chav1961.purelib.basic.GettersAndSettersFactory;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.GettersAndSettersFactory.Instantiator;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.sql.SimpleORMMapper;
import chav1961.purelib.sql.content.ResultSetFactory;
import chav1961.purelib.sql.interfaces.ORMMapper;

public class ReferenceUtil {
	public static <T> T[] loadCSV(final Class<T> resultType, final URI content) throws ContentException, LocalizationException {
		if (resultType == null) {
			throw new NullPointerException("Result type can't be null");
		}
		else if (content == null) {
			throw new NullPointerException("Content uri can't be null");
		}
		else {
			final ContentMetadataInterface	mdi = ContentModelFactory.forAnnotatedClass(resultType);
			final StringBuilder				sb = new StringBuilder();
			final String					query = URIUtils.extractQueryFromURI(content); 
			char 							prefix = '?';
			
			for (Field item : resultType.getFields()) {
				if (item.isAnnotationPresent(ReferenceColumn.class)) {
					try{sb.append(prefix).append(item.getName()).append('=').append(URLEncoder.encode("VARCHAR(100) as \""+item.getAnnotation(ReferenceColumn.class).value()+"\"","UTF8"));
						prefix = '&';
					} catch (UnsupportedEncodingException e) {
						throw new ContentException(e.getLocalizedMessage(),e);
					}
				}
			}
			sb.append("&encoding=UTF8&separator=,&firstlinearenames=true").append('&').append(query);
			
			try(final ResultSet					rs = ResultSetFactory.buildResultSet(null,URI.create(URIUtils.removeQueryFromURI(content).toString()+sb),ResultSet.TYPE_FORWARD_ONLY)) {
				final ResultSetMetaData			rsmd = rs.getMetaData();
				final ContentMetadataInterface	rsMdi = ContentModelFactory.forQueryContentDescription(rsmd);
				final Class<ReferenceUtil>		currentClass = ReferenceUtil.class; 
				final Instantiator<T> 			inst = GettersAndSettersFactory.buildInstantiator(resultType,(m)->allowAccess(m,currentClass,resultType));
				final ContentMetadataInterface	instMdi = ContentModelFactory.forOrdinalClass(resultType);
				final ORMMapper<T>				mapper = new SimpleORMMapper<>(instMdi.getRoot(),rsMdi.getRoot());
				final List<T>					result = new ArrayList<>();
				
				while (rs.next()) {
					final T		line = inst.newInstance();
					
					mapper.fromRecord(line,rs);
					result.add(line);
				}
				return result.toArray(inst.newArray(result.size()));
			} catch (SQLException | IOException |  InstantiationException e) {
				throw new ContentException(e.getLocalizedMessage(),e); 
			}
		}
	}
	
	private static void allowAccess(final Module[] unnamedModules, final Class... classes) {
		for (Class<?> clazz : classes) {
			for (Module m : unnamedModules) {
				clazz.getModule().addExports(clazz.getPackageName(),m);	
			}
		}
	}
}
