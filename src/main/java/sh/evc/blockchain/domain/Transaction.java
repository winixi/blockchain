package sh.evc.blockchain.domain;

import java.io.Serializable;

/**
 * 业务数据模型
 *
 * @author winixi
 */
public class Transaction implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * 唯一标识
   */
  private String id;
  /**
   * 业务数据
   */
  private String businessInfo;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBusinessInfo() {
    return businessInfo;
  }

  public void setBusinessInfo(String businessInfo) {
    this.businessInfo = businessInfo;
  }

  @Override
  public String toString() {
    return "Transaction{" +
            "id='" + id + '\'' +
            ", businessInfo='" + businessInfo + '\'' +
            '}';
  }
}
