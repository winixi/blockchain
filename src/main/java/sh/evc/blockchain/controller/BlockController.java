package sh.evc.blockchain.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.evc.blockchain.domain.Block;
import sh.evc.blockchain.domain.Transaction;
import sh.evc.blockchain.service.BlockCache;
import sh.evc.blockchain.service.BlockService;
import sh.evc.blockchain.service.PowService;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class BlockController {

  @Resource
  private BlockService blockService;

  @Resource
  private PowService powService;

  @Resource
  private BlockCache blockCache;

  /**
   * 查看当前节点区块链数据
   *
   * @return
   */
  @GetMapping("/scan")
  public List<Block> scanBlock() {
    return blockCache.getBlockChain();
  }

  /**
   * 查看当前节点区块链数据
   *
   * @return
   */
  @GetMapping("/data")
  public List<Transaction> scanData() {
    return blockCache.getPackedTransactions();
  }

  /**
   * 创建创世区块
   *
   * @return
   */
  @GetMapping("/create")
  public List<Block> createFirstBlock() {
    blockService.createGenesisBlock();
    return blockCache.getBlockChain();
  }

  /**
   * 工作量证明PoW
   * 挖矿生成新的区块
   */
  @GetMapping("/mine")
  public List<Block> createNewBlock() {
    powService.mine();
    return blockCache.getBlockChain();
  }
}
