package com.messagebus.interactor.pubsub.impl.zookeeper;


public class ZKEventType {
    private int currentIndex;

    public ZKEventType(org.apache.zookeeper.Watcher.Event.EventType eventType) {
        this.currentIndex = eventType.getIntValue();
    }

    public ZKEventType.EventType get() {
        return ZKEventType.EventType.fromIndex(this.currentIndex);
    }

    public static enum EventType {
        None(0),
        NodeCreated(1),
        NodeDelete(2),
        NodeDataChanged(3),
        NodeChildrenChanged(4);

        private int idx;
        private static ZKEventType.EventType[] eventTypes = new ZKEventType.EventType[5];

        private EventType(int idx) {
            this.idx = idx;
        }

        public static ZKEventType.EventType fromIndex(int idx) {
            if (idx >= 0 && idx < eventTypes.length) {
                return eventTypes[idx];
            } else {
                throw new IllegalArgumentException("illegal index : " + idx);
            }
        }

        static {
            eventTypes[0] = None;
            eventTypes[1] = NodeCreated;
            eventTypes[2] = NodeDelete;
            eventTypes[3] = NodeDataChanged;
            eventTypes[4] = NodeChildrenChanged;
        }
    }
}
