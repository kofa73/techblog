package org.sample.lists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class Lists {
	private List<Integer> arrayList = new ArrayList<>();
	private List<Integer> linkedList = new LinkedList<>();
	
	@Setup
	public void setup() {
		for (int i = 0; i < 100_000; i++) {
			arrayList.add(i);
			linkedList.add(i);
		}
	}
	
	@Benchmark
	public Iterator<Integer> arrayListIteratorOnly() {
		return arrayList.iterator();
	}
	
	@Benchmark
	public Iterator<Integer> linkedListIteratorOnly() {
		return linkedList.iterator();
	}
	
	@Benchmark
	@OperationsPerInvocation(100_000)
	public void arrayList(Blackhole blackhole) {
		for (Integer i : arrayList) {
			blackhole.consume(i);
		}
	}
	
	@Benchmark
	@OperationsPerInvocation(100_000)
	public void linkedList(Blackhole blackhole) {
		for (Integer i : linkedList) {
			blackhole.consume(i);
		}
	}
}
