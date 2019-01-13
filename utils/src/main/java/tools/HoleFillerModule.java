package tools;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import lightricks.image.processor.*;

public class HoleFillerModule extends AbstractModule {

    @Override
    protected void configure() {

        install(new FactoryModuleBuilder()
                .implement(HoleFiller.class, HoleFillerLightricks.class)
                .build(HoleFillerFactory.class));
        bind(LibsLoader.class);
    }
}
