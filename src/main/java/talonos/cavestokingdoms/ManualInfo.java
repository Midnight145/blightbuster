package talonos.cavestokingdoms;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mantle.books.BookData;
import mantle.books.BookDataStore;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import talonos.cavestokingdoms.lib.DEFS;

public class ManualInfo
{

    public BookData mats0 = new BookData();
    public BookData mats1 = new BookData();
    public BookData mats2 = new BookData();
    public BookData mats3 = new BookData();
    public BookData mats4 = new BookData();
    public BookData mats5 = new BookData();
    public BookData ben1 = new BookData();
    public BookData ben2 = new BookData();
    public BookData ben3 = new BookData();
    public BookData ben4 = new BookData();
    public BookData taint1 = new BookData();
    public BookData taint2 = new BookData();

    private Document mats0Doc;
    private Document mats1Doc;
    private Document mats2Doc;
    private Document mats3Doc;
    private Document mats4Doc;
    private Document mats5doc;
    private Document ben1doc;
    private Document ben2doc;
    private Document ben3doc;
    private Document ben4doc;
    private Document taint1Doc;
    private Document taint2Doc;

    public ManualInfo()
    {
    	readManuals();
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        Document d = mats0Doc;
        initManual(mats0, "basicManual.0", "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"), d, "tinker:tinkerbook_diary") ;
        d = mats1Doc;
        initManual(mats1, "basicManual.1", "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"), d, "tinker:tinkerbook_diary") ;
        d = mats2Doc;
        initManual(mats2, "basicManual.2", "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"), d, "tinker:tinkerbook_diary") ;
        d = mats3Doc;
        initManual(mats3, "basicManual.3", "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"), d, "tinker:tinkerbook_diary") ;
        d = mats4Doc;
        initManual(mats4, "basicManual.4", "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"), d, "tinker:tinkerbook_diary") ;
        d = mats5doc;
        initManual(mats5, "basicManual.5", "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"), d, "tinker:tinkerbook_diary") ;
        d = ben1doc;
        initManual(ben1, "basicManual.6", "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"), d, "tinker:tinkerbook_diary") ;
        d = ben2doc;
        initManual(ben2, "basicManual.7", "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"), d, "tinker:tinkerbook_diary") ;
        d = ben3doc;
        initManual(ben3, "basicManual.8", "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"), d, "tinker:tinkerbook_diary") ;
        d = ben4doc;
        initManual(ben4, "basicManual.9", "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"), d, "tinker:tinkerbook_diary") ;
        d = taint1Doc;
        initManual(taint1, "basicManual.10", "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"), d, "tinker:tinkerbook_diary") ;
        d = taint2Doc;
        initManual(taint2, "basicManual.11", "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"), d, "tinker:tinkerbook_diary") ;
    }

    public BookData initManual (BookData data, String unlocName, String toolTip, Document xmlDoc, String itemImage)
    {
        //proxy.readManuals();
        data.unlocalizedName = unlocName;
        data.toolTip = unlocName;
        data.modID = "item."+DEFS.MODID;
        data.itemImage = new ResourceLocation(data.modID, itemImage);
        data.doc = xmlDoc;
        BookDataStore.addBook(data);
        return data;
    }
    
    public void readManuals ()
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        mats0Doc = readManual("/assets/cavestokingdoms/manuals/materials_0.xml", dbFactory);
        mats1Doc = readManual("/assets/cavestokingdoms/manuals/materials_1.xml", dbFactory);
        mats2Doc = readManual("/assets/cavestokingdoms/manuals/materials_2.xml", dbFactory);
        mats3Doc = readManual("/assets/cavestokingdoms/manuals/materials_3.xml", dbFactory);
        mats4Doc = readManual("/assets/cavestokingdoms/manuals/materials_4.xml", dbFactory);
        mats5doc = readManual("/assets/cavestokingdoms/manuals/materials_5.xml", dbFactory);
        ben1doc = readManual("/assets/cavestokingdoms/manuals/xillith_1.xml", dbFactory);
        ben2doc = readManual("/assets/cavestokingdoms/manuals/xillith_2.xml", dbFactory);
        ben3doc = readManual("/assets/cavestokingdoms/manuals/xillith_3.xml", dbFactory);
        ben4doc = readManual("/assets/cavestokingdoms/manuals/xillith_4.xml", dbFactory);
    }

    Document readManual (String location, DocumentBuilderFactory dbFactory)
    {
        try
        {
            InputStream stream = CavesToKindgoms.class.getResourceAsStream(location);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

}