package sh.evc.blockchain.service;

import org.java_websocket.WebSocket;
import org.springframework.stereotype.Component;
import sh.evc.blockchain.domain.Block;
import sh.evc.blockchain.domain.Transaction;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class BlockCache {

  /**
   * 当前节点的区块链结构
   */
  private List<Block> blockChain = new CopyOnWriteArrayList<>();

  /**
   * 已打包保存的业务数据集合
   */
  private List<Transaction> packedTransactions = new CopyOnWriteArrayList<>();

  /**
   * 当前节点的socket对象
   */
  private List<WebSocket> socketsList = new CopyOnWriteArrayList<>();

  /**
   * 获取最新的区块，即当前链上最后一个区块
   *
   * @return
   */
  public Block getLatestBlock() {
    return blockChain.size() > 0 ? blockChain.get(blockChain.size() - 1) : null;
  }

  public List<Block> getBlockChain() {
    return blockChain;
  }

  public void setBlockChain(List<Block> blockChain) {
    this.blockChain = blockChain;
  }

  public List<Transaction> getPackedTransactions() {
    return packedTransactions;
  }

  public void setPackedTransactions(List<Transaction> packedTransactions) {
    this.packedTransactions = packedTransactions;
  }

  public List<WebSocket> getSocketsList() {
    return socketsList;
  }

  public void setSocketsList(List<WebSocket> socketsList) {
    this.socketsList = socketsList;
  }

}
