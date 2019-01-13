package lightricks.image.processor;

import com.google.inject.Singleton;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_java;

@Singleton
public class LibsLoader {
    static {
        Loader.load(opencv_java.class);
    }
}
