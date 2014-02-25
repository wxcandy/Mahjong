package org.mahjong.client;


public class Tile {
	private int domain;
	private String pre;
	private String post;
	public Tile(int domain, String pre, String post){
		this.domain = domain;
		this.pre = pre;
		this.post = post;
	}
	
	public String getValue(){
		return pre+post;
	}
	
	public int getDomain() {
		return domain;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Tile)) return false;
		Tile t = (Tile) o;
		return (domain == t.domain) && (pre.equals(t.pre)) && (post.equals(t.post));
	}
	
	public String getPre() {
		return pre;
	}
	
	public int getPost() {
		return Integer.valueOf(post);
	}
	public boolean isEmpty() {
		return this.pre==null&&this.post==null;
	}
 }
