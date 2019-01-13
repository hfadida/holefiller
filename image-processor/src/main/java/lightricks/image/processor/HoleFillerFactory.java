package lightricks.image.processor;

import com.google.inject.assistedinject.Assisted;

public interface HoleFillerFactory {
    HoleFiller create(
            @Assisted("connectivity") int connectivity,
            @Assisted("epsilon") float epsilon,
            @Assisted("z") int z);
}