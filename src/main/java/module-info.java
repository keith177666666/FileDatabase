import dev.keith.IDataBaseProvider;
import dev.keith.fileDataBase.AbstractFileDataBaseObserver;
// import dev.keith.fileDataBase.Example;
import dev.keith.fileDataBase.FileDataBase;

/**
 * Defined to use a more convenient, automatically initialing the File DataBase. <br>
 * How to Use  <br>
 * please write a static block and load dev.keith.DataBaseCore, <br>
 * this is the original DataBase Service loader and add a line like
 * line 24 in this file. <br>
 * Aim to automatically provide and load your observer.<br>
 * DON'T FORGET TO ADD A DEFAULT CONSTRUCTOR IN YOUR OBSERVER!!!
 * Otherwise, it will fail to load and throw a exception (or error)<br>
 */
module FileDatabase {
    requires Database;
    requires org.jetbrains.annotations;

    provides IDataBaseProvider with FileDataBase.Provider;

    uses AbstractFileDataBaseObserver;
    // only a EXAMPLE for automatic initialing the database
    // provides AbstractFileDataBaseObserver with Example;
}