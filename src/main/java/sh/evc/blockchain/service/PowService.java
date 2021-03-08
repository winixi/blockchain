package sh.evc.blockchain.service;

import org.springframework.stereotype.Service;
import sh.evc.blockchain.Config;
import sh.evc.blockchain.Constant;
import sh.evc.blockchain.domain.Block;
import sh.evc.blockchain.domain.Message;
import sh.evc.blockchain.domain.Transaction;
import sh.evc.blockchain.util.CommonUtil;
import sh.evc.blockchain.util.JacksonSerialize;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 共识机制
 * 采用POW即工作量证明实现共识
 *
 * @author Administrator
 */
@Service
public class PowService {

  @Resource
  private Config config;

  @Resource
  private BlockCache blockCache;

  @Resource
  private BlockService blockService;

  @Resource
  private P2PService p2PService;

  /**
   * 通过“挖矿”进行工作量证明，实现节点间的共识
   *
   * @return
   */
  public Block mine() {

    // 封装业务数据集合，记录区块产生的节点信息，临时硬编码实现
    List<Transaction> tsaList = new ArrayList<Transaction>();
    Transaction tsa1 = new Transaction();
    tsa1.setId("1");
    tsa1.setBusinessInfo("这是IP为：" + CommonUtil.getLocalIp() + "，端口号为：" + config.getP2pport() + "的节点挖矿生成的区块");
    tsaList.add(tsa1);
    Transaction tsa2 = new Transaction();
    tsa2.setId("2");
    tsa2.setBusinessInfo("区块链高度为：" + (blockCache.getLatestBlock().getIndex() + 1));
    tsaList.add(tsa2);

    // 定义每次哈希函数的结果
    String newBlockHash = "";
    int nonce = 0;
    long start = System.currentTimeMillis();
    System.out.println("开始挖矿");
    while (true) {
      // 计算新区块hash值
      newBlockHash = blockService.calculateHash(blockCache.getLatestBlock().getHash(), tsaList, nonce);
      // 校验hash值
      if (blockService.isValidHash(newBlockHash)) {
        System.out.println("挖矿完成，正确的hash值：" + newBlockHash);
        System.out.println("挖矿耗费时间：" + (System.currentTimeMillis() - start) + "ms");
        break;
      }
      System.out.println("第" + (nonce + 1) + "次尝试计算的hash值：" + newBlockHash);
      nonce++;
    }
    // 创建新的区块
    Block block = blockService.createNewBlock(nonce, blockCache.getLatestBlock().getHash(), newBlockHash, tsaList);

    //创建成功后，全网广播出去
    Message msg = new Message();
    msg.setType(Constant.RESPONSE_LATEST_BLOCK);
    msg.setData(JacksonSerialize.beanToJson(block));
    p2PService.broadcast(JacksonSerialize.beanToJson(msg));

    return block;
  }

}
