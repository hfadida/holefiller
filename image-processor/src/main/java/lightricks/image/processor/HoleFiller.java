package lightricks.image.processor;

import com.google.inject.Inject;

public abstract class HoleFiller {
    @Inject
    LibsLoader libsLoader;
    abstract public void apply(HoleFillerMat holeMat, HoleFillerMat filledMat);
}
