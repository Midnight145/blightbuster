package talonos.cavestokingdoms;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.w3c.dom.Document;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mantle.books.BookData;
import mantle.books.BookDataStore;
import talonos.cavestokingdoms.lib.DEFS;

public class ManualInfo {

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
    public BookData sarah1 = new BookData();
    public BookData sarah2 = new BookData();
    public BookData dark = new BookData();

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
    private Document sarah1Doc;
    private Document sarah2Doc;
    private Document darkDoc;

    public ManualInfo() {
        this.readManuals();
        Side side = FMLCommonHandler.instance()
            .getEffectiveSide();
        Document d = this.mats0Doc;
        this.initManual(
            this.mats0,
            "basicManual.0",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
        d = this.mats1Doc;
        this.initManual(
            this.mats1,
            "basicManual.1",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
        d = this.mats2Doc;
        this.initManual(
            this.mats2,
            "basicManual.2",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
        d = this.mats3Doc;
        this.initManual(
            this.mats3,
            "basicManual.3",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
        d = this.mats4Doc;
        this.initManual(
            this.mats4,
            "basicManual.4",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
        d = this.mats5doc;
        this.initManual(
            this.mats5,
            "basicManual.5",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
        d = this.ben1doc;
        this.initManual(
            this.ben1,
            "basicManual.6",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
        d = this.ben2doc;
        this.initManual(
            this.ben2,
            "basicManual.7",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
        d = this.ben3doc;
        this.initManual(
            this.ben3,
            "basicManual.8",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
        d = this.ben4doc;
        this.initManual(
            this.ben4,
            "basicManual.9",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
        d = this.taint1Doc;
        this.initManual(
            this.taint1,
            "basicManual.10",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
        d = this.taint2Doc;
        this.initManual(
            this.taint2,
            "basicManual.11",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
        d = this.sarah1Doc;
        this.initManual(
            this.sarah1,
            "basicManual.12",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
        d = this.sarah2Doc;
        this.initManual(
            this.sarah2,
            "basicManual.13",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
        d = this.darkDoc;
        this.initManual(
            this.dark,
            "basicManual.14",
            "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"),
            d,
            "tinker:tinkerbook_diary");
    }

    public BookData initManual(BookData data, String unlocName, String toolTip, Document xmlDoc, String itemImage) {
        // proxy.readManuals();
        data.unlocalizedName = unlocName;
        data.toolTip = unlocName;
        data.modID = "item." + DEFS.MODID;
        data.itemImage = new ResourceLocation(data.modID, itemImage);
        data.doc = xmlDoc;
        BookDataStore.addBook(data);
        return data;
    }

    public void readManuals() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        this.mats0Doc = this.readManual("/assets/cavestokingdoms/manuals/materials_0.xml", dbFactory);
        this.mats1Doc = this.readManual("/assets/cavestokingdoms/manuals/materials_1.xml", dbFactory);
        this.mats2Doc = this.readManual("/assets/cavestokingdoms/manuals/materials_2.xml", dbFactory);
        this.mats3Doc = this.readManual("/assets/cavestokingdoms/manuals/materials_3.xml", dbFactory);
        this.mats4Doc = this.readManual("/assets/cavestokingdoms/manuals/materials_4.xml", dbFactory);
        this.mats5doc = this.readManual("/assets/cavestokingdoms/manuals/materials_5.xml", dbFactory);
        this.ben1doc = this.readManual("/assets/cavestokingdoms/manuals/xillith_1.xml", dbFactory);
        this.ben2doc = this.readManual("/assets/cavestokingdoms/manuals/xillith_2.xml", dbFactory);
        this.ben3doc = this.readManual("/assets/cavestokingdoms/manuals/xillith_3.xml", dbFactory);
        this.ben4doc = this.readManual("/assets/cavestokingdoms/manuals/xillith_4.xml", dbFactory);
        this.sarah1Doc = this.readManual("/assets/cavestokingdoms/manuals/sarah_1.xml", dbFactory);
        this.sarah2Doc = this.readManual("/assets/cavestokingdoms/manuals/sarah_2.xml", dbFactory);
        this.darkDoc = this.readManual("/assets/cavestokingdoms/manuals/dark.xml", dbFactory);
    }

    Document readManual(String location, DocumentBuilderFactory dbFactory) {
        try {
            InputStream stream = CavesToKingdoms.class.getResourceAsStream(location);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement()
                .normalize();

            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
