package sh.evc.blockchain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.java_websocket.WebSocket;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sh.evc.blockchain.Constant;
import sh.evc.blockchain.domain.Block;
import sh.evc.blockchain.domain.Message;
import sh.evc.blockchain.util.JacksonSerialize;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * p2p网络服务类
 *
 * @author winixi
 */
@Service
public class P2PService {

  @Resource
  private BlockService blockService;

  @Resource
  private BlockCache blockCache;

  /**
   * 客户端和服务端共用的消息处理方法
   *
   * @param webSocket
   * @param msg
   * @param sockets
   */
  public void handleMessage(WebSocket webSocket, String msg, List<WebSocket> sockets) {
    try {
      Message message = JacksonSerialize.jsonToBean(msg, Message.class);
      System.out.println("接收到IP地址为：" + webSocket.getRemoteSocketAddress().getAddress().toString()
              + "，端口号为：" + webSocket.getRemoteSocketAddress().getPort() + "的p2p消息："
              + msg);
      switch (message.getType()) {
        //客户端请求查询最新的区块:1
        case Constant.QUERY_LATEST_BLOCK:
          write(webSocket, responseLatestBlockMsg());//服务端调用方法返回最新区块:2
          break;
        //接收到服务端返回的最新区块:2
        case Constant.RESPONSE_LATEST_BLOCK:
          handleBlockResponse(message.getData(), sockets);
          break;
        //客户端请求查询整个区块链:3
        case Constant.QUERY_BLOCKCHAIN:
          write(webSocket, responseBlockChainMsg());//服务端调用方法返回最新区块:4
          break;
        //直接接收到其他节点发送的整条区块链信息:4
        case Constant.RESPONSE_BLOCKCHAIN:
          handleBlockChainResponse(message.getData(), sockets);
          break;
        default:
      }
    } catch (Exception e) {
      System.out.println("处理IP地址为：" + webSocket.getRemoteSocketAddress().getAddress().toString()
              + "，端口号为：" + webSocket.getRemoteSocketAddress().getPort() + "的p2p消息错误:"
              + e.getMessage());
    }
  }

  /**
   * 处理其它节点发送过来的区块信息
   *
   * @param blockData
   * @param sockets
   */
  public synchronized void handleBlockResponse(String blockData, List<WebSocket> sockets) {
    //反序列化得到其它节点的最新区块信息
    Block latestBlockReceived = JacksonSerialize.jsonToBean(blockData, Block.class);
    //当前节点的最新区块
    Block latestBlock = blockCache.getLatestBlock();

    if (latestBlockReceived != null) {
      if (latestBlock != null) {
        //如果接收到的区块高度比本地区块高度大的多
        if (latestBlockReceived.getIndex() > latestBlock.getIndex() + 1) {
          broadcast(queryBlockChainMsg());
          System.out.println("重新查询所有节点上的整条区块链");
        } else if (latestBlockReceived.getIndex() > latestBlock.getIndex() &&
                latestBlock.getHash().equals(latestBlockReceived.getPreviousHash())) {
          if (blockService.addBlock(latestBlockReceived)) {
            broadcast(responseLatestBlockMsg());
          }
          System.out.println("将新接收到的区块加入到本地的区块链");
        }
      } else if (latestBlock == null) {
        broadcast(queryBlockChainMsg());
        System.out.println("重新查询所有节点上的整条区块链");
      }
    }
  }

  /**
   * 处理其它节点发送过来的区块链信息
   *
   * @param blockData
   * @param sockets
   */
  public synchronized void handleBlockChainResponse(String blockData, List<WebSocket> sockets) {
    //反序列化得到其它节点的整条区块链信息
    List<Block> receiveBlockchain = JacksonSerialize.jsonToBean(blockData, new TypeReference<ArrayList<Block>>() {
    });
    if (!CollectionUtils.isEmpty(receiveBlockchain) && blockService.isValidChain(receiveBlockchain)) {
      //根据区块索引先对区块进行排序
      Collections.sort(receiveBlockchain, Comparator.comparingInt(Block::getIndex));

      //其它节点的最新区块
      Block latestBlockReceived = receiveBlockchain.get(receiveBlockchain.size() - 1);
      //当前节点的最新区块
      Block latestBlock = blockCache.getLatestBlock();

      if (latestBlock == null) {
        //替换本地的区块链
        blockService.replaceChain(receiveBlockchain);
      } else {
        //其它节点区块链如果比当前节点的长，则处理当前节点的区块链
        if (latestBlockReceived.getIndex() > latestBlock.getIndex()) {
          if (latestBlock.getHash().equals(latestBlockReceived.getPreviousHash())) {
            if (blockService.addBlock(latestBlockReceived)) {
              broadcast(responseLatestBlockMsg());
            }
            System.out.println("将新接收到的区块加入到本地的区块链");
          } else {
            // 用长链替换本地的短链
            blockService.replaceChain(receiveBlockchain);
          }
        }
      }
    }
  }

  /**
   * 全网广播消息
   *
   * @param message
   */
  public void broadcast(String message) {
    List<WebSocket> socketsList = this.getSockets();
    if (CollectionUtils.isEmpty(socketsList)) {
      return;
    }
    System.out.println("======全网广播消息开始：");
    for (WebSocket socket : socketsList) {
      this.write(socket, message);
    }
    System.out.println("======全网广播消息结束");
  }

  /**
   * 向其它节点发送消息
   *
   * @param ws
   * @param message
   */
  public void write(WebSocket ws, String message) {
    System.out.println("发送给IP地址为：" + ws.getRemoteSocketAddress().getAddress().toString()
            + "，端口号为：" + ws.getRemoteSocketAddress().getPort() + " 的p2p消息:" + message);
    ws.send(message);
  }

  /**
   * 查询整条区块链
   *
   * @return
   */
  public String queryBlockChainMsg() {
    return JacksonSerialize.beanToJson(new Message(Constant.QUERY_BLOCKCHAIN));
  }

  /**
   * 返回整条区块链数据
   *
   * @return
   */
  public String responseBlockChainMsg() {
    Message msg = new Message();
    msg.setType(Constant.RESPONSE_BLOCKCHAIN);
    msg.setData(JacksonSerialize.beanToJson(blockCache.getBlockChain()));
    return JacksonSerialize.beanToJson(msg);
  }

  /**
   * 查询最新的区块
   *
   * @return
   */
  public String queryLatestBlockMsg() {
    return JacksonSerialize.beanToJson(new Message(Constant.QUERY_LATEST_BLOCK));
  }

  /**
   * 返回最新的区块
   *
   * @return
   */
  public String responseLatestBlockMsg() {
    Message msg = new Message();
    msg.setType(Constant.RESPONSE_LATEST_BLOCK);
    Block b = blockCache.getLatestBlock();
    msg.setData(JacksonSerialize.beanToJson(b));
    return JacksonSerialize.beanToJson(msg);
  }

  public List<WebSocket> getSockets() {
    return blockCache.getSocketsList();
  }
}
