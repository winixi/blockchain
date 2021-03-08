package sh.evc.blockchain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 配置
 *
 * @author winixi
 * @date 2021/3/8 3:15 PM
 */
@Configuration
public class Config {

  /**
   * 挖矿的难度系数
   */
  @Value("${block.difficulty}")
  private int difficulty;

  /**
   * 当前节点p2pserver端口号
   */
  @Value("${block.p2pport}")
  private int p2pport;

  /**
   * 要连接的节点地址
   */
  @Value("${block.address}")
  private String address;

  public int getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(int difficulty) {
    this.difficulty = difficulty;
  }

  public int getP2pport() {
    return p2pport;
  }

  public void setP2pport(int p2pport) {
    this.p2pport = p2pport;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
