package com.messagebus.interactor.pubsub.impl.zookeeper;

import com.messagebus.common.ExceptionHelper;
import com.messagebus.interactor.pubsub.IPubSubListener;
import com.messagebus.interactor.pubsub.IPubSuber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;

public class LongLiveZookeeper implements IPubSuber {
    private static final Log logger = LogFactory.getLog(LongLiveZookeeper.class);
    private CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zooKeeper;
    private String host;
    private int port;
    private List<String> watchedPaths;

    public LongLiveZookeeper() {
    }

    private void init() {
        try {
            this.zooKeeper = new ZooKeeper(this.host + ":" + this.port, 30000, new LongLiveZookeeper.SessionWatcher());
            this.watchedPaths = new ArrayList();
        } catch (IOException var2) {
            throw new RuntimeException("[createZKClient] occurs a IOException : " + var2.getMessage());
        }
    }

    public synchronized void open() {
        this.latch = new CountDownLatch(1);
        this.init();

        try {
            this.latch.await(30L, TimeUnit.SECONDS);
        } catch (InterruptedException var5) {
            logger.error("[getZKInstance] occurs a InterruptedException : " + var5.getMessage());
        } finally {
            this.latch = null;
        }

    }

    public synchronized void close() {
        try {
            if (this.zooKeeper != null) {
                this.zooKeeper.close();
                this.zooKeeper = null;
            }
        } catch (InterruptedException var2) {
            logger.error("[close] occurs a InterruptedException : " + var2.getMessage());
        }

    }

    public void watch(String[] paths, IPubSubListener listener) {
        try {
            LongLiveZookeeper.PathWatcher watcher = new LongLiveZookeeper.PathWatcher(this.zooKeeper, listener);
            logger.debug("zooKeeper : " + this.zooKeeper);
            String[] arr$ = paths;
            int len$ = paths.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String path = arr$[i$];
                if (!this.watchedPaths.contains(path)) {
                    this.zooKeeper.exists(path, watcher);
                    this.watchedPaths.add(path);
                }
            }
        } catch (KeeperException var8) {
            logger.error("[KeeperException] occurs a KeeperException : " + var8.getMessage());
        } catch (InterruptedException var9) {
            logger.error("[InterruptedException] occurs a InterruptedException : " + var9.getMessage());
        } catch (Exception var10) {
            logger.error("[watchPaths] occurs a Exception : " + var10.getMessage());
        }

    }

    public boolean isAlive() {
        return this.zooKeeper != null && this.zooKeeper.getState().equals(States.CONNECTED);
    }

    public byte[] get(String path) {
        try {
            Stat stat = this.zooKeeper.exists(path, false);
            if (stat == null) {
                throw new IllegalStateException("the path : " + path + " is not exists!");
            } else {
                return this.zooKeeper.getData(path, (Watcher)null, (Stat)null);
            }
        } catch (KeeperException var3) {
            ExceptionHelper.logException(logger, var3, "get");
            throw new RuntimeException(var3);
        } catch (InterruptedException var4) {
            return new byte[0];
        }
    }

    public boolean exists(String key) {
        try {
            return this.zooKeeper.exists(key, false) != null;
        } catch (KeeperException var3) {
            ExceptionHelper.logException(logger, var3, "exists");
            throw new RuntimeException(var3);
        } catch (InterruptedException var4) {
            return false;
        }
    }

    public void set(String path, byte[] data) {
        try {
            logger.info("[setConfig] path is : " + path);
            Stat stat = this.zooKeeper.exists(path, false);
            if (stat == null) {
                this.zooKeeper.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } else {
                int version = stat.getVersion();
                this.zooKeeper.setData(path, data, version);
            }
        } catch (KeeperException var5) {
            logger.error("[setConfig] occurs a KeeperException : " + var5.getMessage());
        } catch (InterruptedException var6) {
            logger.error("[setConfig] occurs a InterruptedException : " + var6.getMessage());
        }

    }

    public void publish(String path, byte[] newData) {
        try {
            logger.info("[setConfig] path is : " + path);
            Stat stat = this.zooKeeper.exists(path, false);
            if (stat == null) {
                this.zooKeeper.create(path, newData, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } else {
                int version = stat.getVersion();
                this.zooKeeper.setData(path, newData, version);
            }
        } catch (KeeperException var5) {
            logger.error("[setConfig] occurs a KeeperException : " + var5.getMessage());
        } catch (InterruptedException var6) {
            logger.error("[setConfig] occurs a InterruptedException : " + var6.getMessage());
        }

    }

    public void createNode(String path) throws Exception {
        Stat stat = this.zooKeeper.exists(path, false);
        if (stat == null) {
            this.zooKeeper.create(path, (byte[])null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private static class PathWatcher implements Watcher {
        private ZooKeeper zooKeeper;
        private IPubSubListener listener;

        public PathWatcher(ZooKeeper zooKeeper, IPubSubListener listener) {
            this.zooKeeper = zooKeeper;
            this.listener = listener;
        }

        public void process(WatchedEvent watchedEvent) {
            String path = watchedEvent.getPath();
            if (path != null) {
                LongLiveZookeeper.logger.debug("[process] path : " + path + "changed");

                try {
                    switch(watchedEvent.getType()) {
                    case NodeDataChanged:
                    case NodeCreated:
                    case NodeDeleted:
                        byte[] data = this.zooKeeper.getData(path, false, (Stat)null);
                        ZKEventType eventType = new ZKEventType(watchedEvent.getType());
                        Map<String, Object> params = new HashMap(1);
                        params.put("eventType", eventType);
                        this.listener.onChange(path, data, params);
                    }
                } catch (KeeperException var19) {
                    ExceptionHelper.logException(LongLiveZookeeper.logger, var19, "process");
                    throw new RuntimeException(var19);
                } catch (InterruptedException var20) {
                } finally {
                    try {
                        this.zooKeeper.exists(path, this);
                    } catch (KeeperException var17) {
                        ExceptionHelper.logException(LongLiveZookeeper.logger, var17, "process");
                    } catch (InterruptedException var18) {
                        ExceptionHelper.logException(LongLiveZookeeper.logger, var18, "process");
                    }

                }

            }
        }
    }

    private class SessionWatcher implements Watcher {
        private SessionWatcher() {
        }

        public synchronized void process(WatchedEvent watchedEvent) {
            if (watchedEvent.getState() == KeeperState.SyncConnected) {
                if (LongLiveZookeeper.this.latch != null) {
                    LongLiveZookeeper.this.latch.countDown();
                }
            } else if (watchedEvent.getState() == KeeperState.Expired) {
                LongLiveZookeeper.this.close();
                LongLiveZookeeper.this.init();
            }

        }
    }
}
