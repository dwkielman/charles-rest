/*
 * Copyright (c) 2016, Mihai Emil Andronache
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1)Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *  2)Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  3)Neither the name of charles-github-ejb nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.amihaiemil.charles.github;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Test;
import org.mockito.Mockito;

import com.jcabi.github.Issue;
import com.jcabi.github.Repo;

/**
 * Unit tests for {@link AuthorOwnerCheck}
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 1.0.0
 *
 */
public class AuthorOwnerCheckTestCase {

    /**
     * AuthorOwnerCheck can tell when the command author is owner of the repo.
     * @throws Exception If something goes wrong.
     */
	@Test
	public void authorIsRepoOwner() throws Exception {
    	AuthorOwnerCheck aoc = new AuthorOwnerCheck(
    		this.mockCommand("amihaiemil", "amihaiemil", false),
    		Mockito.mock(SendReply.class)
    	);
    	assertTrue(aoc.perform());
    }
	
    /**
     * AuthorOwnerCheck can tell when the repo is a fork.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void repoIsAFork() throws Exception {
        SendReply sr = Mockito.mock(SendReply.class);
        Mockito.when(sr.perform()).thenReturn(true);
        AuthorOwnerCheck aoc = new AuthorOwnerCheck(
        	this.mockCommand("amihaiemil", "amihaiemil", true),
        	sr
        );
        assertFalse(aoc.perform());
    }

	/**
	 * AuthorOwnerCheck can tell when the command author is NOT owner of the repo.
	 * @throws Exception If something goes wrong.
	 */
	@Test
	public void authorIsNotRepoOwner() throws Exception {
		SendReply sr = Mockito.mock(SendReply.class);
		Mockito.when(sr.perform()).thenReturn(true);
    	AuthorOwnerCheck aoc = new AuthorOwnerCheck(
    		this.mockCommand("someone", "amihaiemil", false),
    		sr
    	);
    	assertFalse(aoc.perform());
	}
	
	/**
	 * Mock a command for the unit tests.
	 * @param author Author of the command.
	 * @param repoOwner Repository owner.
	 * @param fork Is the repository a fork or not?
	 * @return Command mock.
	 * @throws IOException If something goes wrong.
	 */
	public Command mockCommand(String author, String repoOwner, boolean fork) throws IOException {
		JsonObject repoJson = Json.createObjectBuilder()
			.add(
				"owner",
				Json.createObjectBuilder().add("login", repoOwner).build()
			)
			.add("fork", fork)
			.build();
		Repo repo = Mockito.mock(Repo.class);
		Mockito.when(repo.json()).thenReturn(repoJson);
		Issue issue = Mockito.mock(Issue.class);
		Mockito.when(issue.repo()).thenReturn(repo);
		Command command = Mockito.mock(Command.class);
		Mockito.when(command.authorLogin()).thenReturn(author);
		Mockito.when(command.issue()).thenReturn(issue);
		return command;
	}
}