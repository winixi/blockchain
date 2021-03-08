package sh.evc.blockchain;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import sh.evc.blockchain.service.P2PClient;
import sh.evc.blockchain.service.P2PServer;

import javax.annotation.Resource;

/**
 * 启动自动运行
 *
 * @author winixi
 * @date 2021/3/8 3:09 PM
 */
@Component
@Order(1)
public class AutoRunner implements CommandLineRunner {

  @Resource
  private Config config;

  @Resource
  private P2PServer p2PServer;

  @Resource
  private P2PClient p2PClient;

  @Override
  public void run(String... args) {
    p2PServer.initP2PServer(config.getP2pport());
    p2PClient.connectToPeer(config.getAddress());

    System.out.println("难度系数: " + config.getDifficulty());
    System.out.println("端口号: " + config.getP2pport());
    System.out.println("节点地址: " + config.getAddress());

  }
}
