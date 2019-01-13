package tools;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lightricks.image.processor.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;

@Singleton
public class HoleFillerRunner {

    private static final String INPUT_IMAGE_KEY = "input-image";
    private static final String INPUT_HOLE_MASK_KEY = "input-hole-mask";
    private static final String EPSILON_KEY = "epsilon";
    private static final String Z_KEY = "z";
    private static final String CONNECTIVITY_TYPE_KEY = "connectivity-type";
    private static final String FIXED_IMAGE_SUFFIX = "_fixed";

    @Inject
    HoleFillerFactory holeFillerFactory;

    public void run(HoleFillerParams params) throws Exception {
        HoleFiller holeFiller = holeFillerFactory.create(params.getConnectivity(), params.getEpsilon(), params.getZ());

        HoleFillerMat holeMat = HoleFillerMat.imread(params.getImgFilename(), params.getMaskFilename());
        HoleFillerMat res = new HoleFillerMat(holeMat.size());
        holeFiller.apply(holeMat, res);

        String imgBaseName = FilenameUtils.getBaseName(params.getImgFilename());
        String imgPath = FilenameUtils.getFullPath(params.getImgFilename());
        HoleFillerMat.imwrite(imgPath + imgBaseName + FIXED_IMAGE_SUFFIX +  "." + FilenameUtils.getExtension(params.getImgFilename()), res);
    }

    static private Options createOptions() {
        Options options = new Options();

        options.addOption(Option.builder()
                .longOpt(INPUT_IMAGE_KEY)
                .required()
                .hasArg()
                .desc("path to an input image file")
                .build());

        options.addOption(Option.builder()
                .longOpt(INPUT_HOLE_MASK_KEY)
                .required()
                .hasArg()
                .desc("path to an input hole mask file")
                .build());

        options.addOption(Option.builder()
                .longOpt(EPSILON_KEY)
                .required()
                .hasArg()
                .desc("weighting function epsilon value")
                .build());

        options.addOption(Option.builder()
                .longOpt(Z_KEY)
                .valueSeparator()
                .required()
                .hasArg()
                .desc("weighting function exponent")
                .build());

        options.addOption(Option.builder()
                .longOpt(CONNECTIVITY_TYPE_KEY)
                .required()
                .hasArg()
                .desc("connectivity type (4/8-way)")
                .build());
        return options;
    }

    private static HoleFillerParams parseFromCommandLine(CommandLineParser parser, Options options, String[] args) {
        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("HoleFillerRunner", options);
        }

        HoleFillerParams params = new HoleFillerParams();
        params.setConnectivity(Integer.parseInt(line.getOptionValue(CONNECTIVITY_TYPE_KEY)));
        params.setEpsilon(Float.parseFloat(line.getOptionValue(EPSILON_KEY)));
        params.setImgFilename(line.getOptionValue(INPUT_IMAGE_KEY));
        params.setMaskFilename(line.getOptionValue(INPUT_HOLE_MASK_KEY));
        params.setZ(Integer.parseInt(line.getOptionValue(Z_KEY)));

        return params;
    }

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();

        Injector injector = Guice.createInjector(new HoleFillerModule());
        HoleFillerRunner runner = injector.getInstance(HoleFillerRunner.class);

        HoleFillerParams params = parseFromCommandLine(parser, options, args);

        try {
            runner.run(params);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
