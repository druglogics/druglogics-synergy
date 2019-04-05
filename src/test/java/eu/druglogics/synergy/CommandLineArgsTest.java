package eu.druglogics.synergy;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CommandLineArgsTest {

    @Test
    public void test_all_arguments() {
        CommandLineArgs args = new CommandLineArgs();

        String[] argv = {"-p", "project", "-i", "inputDir"};
        JCommander.newBuilder().addObject(args).build().parse(argv);

        Assert.assertEquals(args.getProjectName(), "project");
        Assert.assertEquals(args.getDirectoryInput(), "inputDir");
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void test_no_arguments() {
        exception.expect(ParameterException.class);

        CommandLineArgs args = new CommandLineArgs();
        JCommander.newBuilder().addObject(args).build().parse("");
    }

    @Test
    public void test_missing_required_arguments() {
        exception.expect(ParameterException.class);

        CommandLineArgs args = new CommandLineArgs();
        String[] argv = {"-p", "project", "-c", "config"};
        JCommander.newBuilder().addObject(args).build().parse(argv);
    }

}
