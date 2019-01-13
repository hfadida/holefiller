package lightricks.image.processor;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class TestHoleFillerModule extends AbstractModule {

    @Override
    protected void configure() {

        install(new FactoryModuleBuilder()
                .implement(HoleFiller.class, HoleFillerLightricks.class)
                .build(HoleFillerFactory.class));
        bind(LibsLoader.class);
    }
}
