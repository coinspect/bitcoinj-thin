package co.rsk.bitcoinj.script;

import co.rsk.bitcoinj.core.BtcECKey;
import co.rsk.bitcoinj.core.Sha256Hash;
import co.rsk.bitcoinj.core.Utils;
import co.rsk.bitcoinj.script.RedeemScriptParser.MultiSigType;
import co.rsk.bitcoinj.script.RedeemScriptParser.ScriptType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RedeemScriptParserFactoryTest {

    private final List<BtcECKey> defaultFedBtcECKeyList = new ArrayList<>();
    private final List<BtcECKey> erpFedBtcECKeyList = new ArrayList<>();
    private final BtcECKey ecKey1 = BtcECKey.fromPrivate(BigInteger.valueOf(100));
    private final BtcECKey ecKey2 = BtcECKey.fromPrivate(BigInteger.valueOf(200));
    private final BtcECKey ecKey3 = BtcECKey.fromPrivate(BigInteger.valueOf(300));
    private final BtcECKey ecKey4 = BtcECKey.fromPrivate(BigInteger.valueOf(400));
    private final BtcECKey ecKey5 = BtcECKey.fromPrivate(BigInteger.valueOf(500));
    private final BtcECKey ecKey6 = BtcECKey.fromPrivate(BigInteger.valueOf(600));
    private final BtcECKey ecKey7 = BtcECKey.fromPrivate(BigInteger.valueOf(700));
    private final BtcECKey ecKey8 = BtcECKey.fromPrivate(BigInteger.valueOf(800));

    @Before
    public void setUp() {
        defaultFedBtcECKeyList.add(ecKey1);
        defaultFedBtcECKeyList.add(ecKey2);
        defaultFedBtcECKeyList.add(ecKey3);
        erpFedBtcECKeyList.add(ecKey4);
        erpFedBtcECKeyList.add(ecKey5);
        erpFedBtcECKeyList.add(ecKey6);
        erpFedBtcECKeyList.add(ecKey7);
        erpFedBtcECKeyList.add(ecKey8);
    }

    @Test
    public void create_RedeemScriptParser_object_from_fast_bridge_multiSig_chunk() {
        byte[] data = Sha256Hash.of(new byte[]{1}).getBytes();
        Script fastBridgeRedeemScript = RedeemScriptUtils.createFastBridgeRedeemScript(
            data,
            defaultFedBtcECKeyList
        );

        RedeemScriptParser parser = RedeemScriptParserFactory.get(fastBridgeRedeemScript.getChunks());
        Assert.assertEquals(MultiSigType.FAST_BRIDGE_MULTISIG, parser.getMultiSigType());
        Assert.assertEquals(ScriptType.REDEEM_SCRIPT, parser.getScriptType());
    }

    @Test
    public void create_RedeemScriptParser_object_from_standard_multiSig_chunk() {
        Script redeemScript = RedeemScriptUtils.createStandardRedeemScript(defaultFedBtcECKeyList);
        RedeemScriptParser parser = RedeemScriptParserFactory.get(redeemScript.getChunks());

        Assert.assertEquals(MultiSigType.STANDARD_MULTISIG, parser.getMultiSigType());
        Assert.assertEquals(ScriptType.REDEEM_SCRIPT, parser.getScriptType());
    }

    @Test
    public void create_RedeemScriptParser_object_from_erp_multiSig_chunk() {
        Script redeemScript = RedeemScriptUtils.createErpRedeemScript(
            defaultFedBtcECKeyList,
            erpFedBtcECKeyList,
            500L
        );

        RedeemScriptParser parser = RedeemScriptParserFactory.get(redeemScript.getChunks());

        Assert.assertEquals(MultiSigType.ERP_FED, parser.getMultiSigType());
        Assert.assertEquals(ScriptType.REDEEM_SCRIPT, parser.getScriptType());
    }

    @Test
    public void create_RedeemScriptParser_object_from_erp_fast_bridge_multiSig_chunk() {
        Script redeemScript = RedeemScriptUtils.createFastBridgeErpRedeemScript(
            defaultFedBtcECKeyList,
            erpFedBtcECKeyList,
            500L,
            Sha256Hash.of(new byte[]{1}).getBytes()
        );

        RedeemScriptParser parser = RedeemScriptParserFactory.get(redeemScript.getChunks());

        Assert.assertEquals(MultiSigType.FAST_BRIDGE_ERP_FED, parser.getMultiSigType());
        Assert.assertEquals(ScriptType.REDEEM_SCRIPT, parser.getScriptType());
    }

    @Test
    public void create_RedeemScriptParser_object_from_fast_bridge_P2SH_chunk() {
        byte[] data = Sha256Hash.of(new byte[]{1}).getBytes();
        Script fastBridgeRedeemScript = RedeemScriptUtils.createFastBridgeRedeemScript(
            data,
            defaultFedBtcECKeyList
        );

        Script spk = ScriptBuilder.createP2SHOutputScript(
            2,
            Arrays.asList(ecKey1, ecKey2, ecKey3)
        );

        Script inputScript = spk.createEmptyInputScript(null, fastBridgeRedeemScript);
        RedeemScriptParser parser = RedeemScriptParserFactory.get(inputScript.getChunks());

        Assert.assertEquals(MultiSigType.FAST_BRIDGE_MULTISIG, parser.getMultiSigType());
        Assert.assertEquals(ScriptType.P2SH, parser.getScriptType());
    }

    @Test
    public void create_RedeemScriptParser_object_from_standard_P2SH_chunk() {
        Script redeemScript = RedeemScriptUtils.createStandardRedeemScript(defaultFedBtcECKeyList);

        Script spk = ScriptBuilder.createP2SHOutputScript(
            2,
            Arrays.asList(ecKey1, ecKey2, ecKey3)
        );

        Script inputScript = spk.createEmptyInputScript(null, redeemScript);
        RedeemScriptParser parser = RedeemScriptParserFactory.get(inputScript.getChunks());

        Assert.assertEquals(MultiSigType.STANDARD_MULTISIG, parser.getMultiSigType());
        Assert.assertEquals(ScriptType.P2SH, parser.getScriptType());
    }

    @Test
    public void create_RedeemScriptParser_object_from_erp_P2SH_chunk() {
        Script erpRedeemScript = RedeemScriptUtils.createErpRedeemScript(
            defaultFedBtcECKeyList,
            erpFedBtcECKeyList,
            500L
        );

        Script spk = ScriptBuilder.createP2SHOutputScript(
            2,
            Arrays.asList(ecKey1, ecKey2, ecKey3)
        );

        Script inputScript = spk.createEmptyInputScript(null, erpRedeemScript);
        RedeemScriptParser parser = RedeemScriptParserFactory.get(inputScript.getChunks());

        Assert.assertEquals(MultiSigType.ERP_FED, parser.getMultiSigType());
        Assert.assertEquals(ScriptType.P2SH, parser.getScriptType());
    }

    @Test
    public void create_RedeemScriptParser_object_from_fast_bridge_erp_P2SH_chunk() {
        Script fastBridgeErpRedeemScript = RedeemScriptUtils.createFastBridgeErpRedeemScript(
            defaultFedBtcECKeyList,
            erpFedBtcECKeyList,
            500L,
            Sha256Hash.of(new byte[]{1}).getBytes()
        );

        Script spk = ScriptBuilder.createP2SHOutputScript(
            2,
            Arrays.asList(ecKey1, ecKey2, ecKey3)
        );

        Script inputScript = spk.createEmptyInputScript(null, fastBridgeErpRedeemScript);
        RedeemScriptParser parser = RedeemScriptParserFactory.get(inputScript.getChunks());

        Assert.assertEquals(MultiSigType.FAST_BRIDGE_ERP_FED, parser.getMultiSigType());
        Assert.assertEquals(ScriptType.P2SH, parser.getScriptType());
    }

    @Test
    public void create_RedeemScriptParser_object_from_custom_redeem_script_no_multiSig() {
        Script redeemScript = RedeemScriptUtils.createCustomRedeemScript(defaultFedBtcECKeyList);
        RedeemScriptParser parser = RedeemScriptParserFactory.get(redeemScript.getChunks());

        Assert.assertEquals(MultiSigType.NO_MULTISIG_TYPE, parser.getMultiSigType());
    }

    @Test
    public void create_RedeemScriptParser_object_from_custom_redeem_script_insufficient_chunks() {
        Script redeemScript = new Script(new byte[2]);
        RedeemScriptParser parser = RedeemScriptParserFactory.get(redeemScript.getChunks());

        Assert.assertEquals(MultiSigType.NO_MULTISIG_TYPE, parser.getMultiSigType());
        Assert.assertEquals(ScriptType.UNDEFINED, parser.getScriptType());
    }

    @Test
    public void create_RedeemScriptParser_object_from_hardcoded_testnet_redeem_script() {
        final byte[] ERP_TESTNET_REDEEM_SCRIPT_BYTES = Utils.HEX.decode("6453210208f40073a9e43b3e9103acec79767a6de9b0409749884e989960fee578012fce210225e892391625854128c5c4ea4340de0c2a70570f33db53426fc9c746597a03f42102afc230c2d355b1a577682b07bc2646041b5d0177af0f98395a46018da699b6da210344a3c38cd59afcba3edcebe143e025574594b001700dec41e59409bdbd0f2a0921039a060badbeb24bee49eb2063f616c0f0f0765d4ca646b20a88ce828f259fcdb955670300cd50b27552210216c23b2ea8e4f11c3f9e22711addb1d16a93964796913830856b568cc3ea21d3210275562901dd8faae20de0a4166362a4f82188db77dbed4ca887422ea1ec185f1421034db69f2112f4fb1bb6141bf6e2bd6631f0484d0bd95b16767902c9fe219d4a6f5368ae");
        Script erpTestnetRedeemScript = new Script(ERP_TESTNET_REDEEM_SCRIPT_BYTES);

        RedeemScriptParser parser = RedeemScriptParserFactory.get(erpTestnetRedeemScript.getChunks());

        Assert.assertEquals(MultiSigType.NO_MULTISIG_TYPE, parser.getMultiSigType());
        Assert.assertEquals(ScriptType.UNDEFINED, parser.getScriptType());
    }
}
