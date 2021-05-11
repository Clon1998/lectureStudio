package org.lecturestudio.web.api.data.bind;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class JsonConfig implements ContextResolver<Jsonb> {

	@Override
	public Jsonb getContext(Class<?> aClass) {
		JsonbConfig config = new JsonbConfig()
				.withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {

					@Override
					public boolean isVisible(Method method) {
						return false;
					}

					@Override
					public boolean isVisible(Field field) {
						return true;
					}
				});

		config.withSerializers(
				new ClassroomServiceSerializer(),
				new HttpResourceFileSerializer(),
				new WebMessageSerializer()
		);
		config.withDeserializers(
				new ClassroomServiceDeserializer(),
				new HttpResourceFileDeserializer(),
				new WebMessageDeserializer()
		);

		return JsonbBuilder.create(config);
	}
}
