package com.kaviddiss.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Bolt filters out a predefined set of words.
 * @author davidk
 */
public class IgnoreWordsBolt extends BaseRichBolt {

    private Set<String> IGNORE_LIST = new HashSet<String>(Arrays.asList(new String[] {
            "http", "the", "you", "que", "and", "for", "that", "like", "have", "this", "just", "with", "all", "get", "about",
            "can", "was", "not", "your", "but", "are", "one", "what", "out", "when", "get", "lol", "now", "para", "por",
            "want", "will", "know", "good", "from", "las", "don", "people", "got", "why", "con", "time", "would",
    }));
    private OutputCollector collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        String lang = (String) input.getValueByField("lang");
        String word = (String) input.getValueByField("word");
        if (!IGNORE_LIST.contains(word)) {
            collector.emit(new Values(lang, word));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("lang", "word"));
    }
}
