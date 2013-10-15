package bench;

import java.io.InputStream;

import org.spoofax.jsglr.client.ParseTable;
import org.spoofax.sunshine.CompilerException;
import org.spoofax.sunshine.Environment;
import org.spoofax.sunshine.parser.model.IParseTableProvider;

public class ClassRelativeParseTableProvider implements IParseTableProvider {

    private String pathToTable;
    private ParseTable table;

    public ClassRelativeParseTableProvider(String pathToTable) {
        this.pathToTable = pathToTable;
    }

    @Override
    public ParseTable getParseTable() {
        if (this.table != null)
            return this.table;

        InputStream stream;
        ParseTable table;
        try {
            stream = this.getClass().getClassLoader().getResourceAsStream(pathToTable);
            table = Environment.INSTANCE().parseTableMgr.loadFromStream(stream);
        } catch (Exception e) {
            throw new CompilerException("Could not load parse table", e);
        }

        this.table = table;
        return table;
    }

}
