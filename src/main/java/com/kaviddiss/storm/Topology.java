package com.kaviddiss.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import com.google.common.base.Preconditions;

public class Topology {

	static final String TOPOLOGY_NAME = "storm-twitter-word-count";

	public static void main(String[] args) {
		Config config = new Config();
		config.setMessageTimeoutSecs(120);

        Preconditions.checkArgument(args.length == 2);

        // Read twitter credentials from command line args:
		String twitterUsername = args[0];
		String twitterPassword = args[1];

        Preconditions.checkArgument(!twitterUsername.equals(""));
        Preconditions.checkArgument(!twitterPassword.equals(""));

		TopologyBuilder b = new TopologyBuilder();
		b.setSpout("TwitterSampleSpout", new TwitterSampleSpout(twitterUsername, twitterPassword));
        b.setBolt("WordSplitterBolt", new WordSplitterBolt(5)).shuffleGrouping("TwitterSampleSpout");
        b.setBolt("IgnoreWordsBolt", new IgnoreWordsBolt()).shuffleGrouping("WordSplitterBolt");
        b.setBolt("WordCounterBolt", new WordCounterBolt(10, 5 * 60, 50)).shuffleGrouping("IgnoreWordsBolt");

		final LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(TOPOLOGY_NAME, config, b.createTopology());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				cluster.killTopology(TOPOLOGY_NAME);
				cluster.shutdown();
			}
		});

	}

}
