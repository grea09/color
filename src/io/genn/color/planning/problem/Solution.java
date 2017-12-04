/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.problem;

/**
 *
 * @author antoine
 */
public interface Solution {

	public int level();

	public Plan working(); //Working plan

	public Plan plan(); //Solution plan (null if not completed)

	public boolean completed();
	public boolean success();
	
	public void end(boolean success);
}
