import org.junit.Test;

import FileParser.Parser;

import static org.junit.Assert.assertEquals;

import java.io.File;

public class ParserTest {
    @Test
    public void parseFolderTest(){
        assertEquals(Parser.parse(new File("src\\test\\java\\TestFiles")).size(), 3);
    }
}
