package org.hive2hive.core.test.process.common.massages;

import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

import org.hive2hive.core.network.messages.AcceptanceReply;
import org.hive2hive.core.network.messages.direct.BaseDirectMessage;
import org.hive2hive.core.test.H2HTestData;
import org.hive2hive.core.test.network.messages.BaseMessageTest;

/**
 * This test message is used to put locally some content into the target node which is given through the peer
 * address. This behavior is used to check if this message is actually sent to the target and executed there
 * successfully. For further details see
 * {@link BaseMessageTest#testSendingAnAsynchronousMessageWithNoReplyToTargetNode()}
 * 
 * @author Seppi
 * 
 */
public class TestDirectMessage extends BaseDirectMessage {

	private static final long serialVersionUID = 880089170139661640L;

	private final String contentKey;
	private final H2HTestData wrapper;

	public TestDirectMessage(String targetKey, PeerAddress targetAddress, String contentKey,
			H2HTestData wrapper, boolean needsRedirectedSend) {
		super(targetKey, targetAddress, needsRedirectedSend);
		this.contentKey = contentKey;
		this.wrapper = wrapper;
	}

	@Override
	public void run() {
		Number160 lKey = Number160.createHash(networkManager.getNodeId());
		Number160 cKey = Number160.createHash(contentKey);
		networkManager.getDataManager().put(lKey, Number160.ZERO, cKey, wrapper, null).awaitUninterruptibly();
	}

	@Override
	public AcceptanceReply accept() {
		return AcceptanceReply.OK;
	}

	@Override
	public boolean checkSignature(byte[] data, byte[] signature, String userId) {
		if (!networkManager.getUserId().equals(userId)) {
			return false;
		} else {
			return verify(data, signature, networkManager.getPublicKey());
		}
	}
	
}
