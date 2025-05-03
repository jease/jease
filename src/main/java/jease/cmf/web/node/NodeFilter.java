/*
    Copyright (C) 2016 maik.jablonski@jease.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jease.cmf.web.node;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import jease.cmf.domain.Node;

/**
 * Filters all Nodes which are not in the initial array of accepted Nodes.
 */
public class NodeFilter {

    private Set<Class<? extends Node>> acceptedNodes = new HashSet<>();

    public NodeFilter(final Node[] validNodes) {
        for (Node node : validNodes) {
            acceptedNodes.add(node.getClass());
        }
    }

    /**
     * Returns only valid Nodes from given Nodes which were registered with this
     * NodeFilter.
     */
    public Node[] apply(final Node[] nodes) {
        return Stream.of(nodes).filter($node -> isAccepted($node))
                .toArray($size -> new Node[$size]);
    }

    /**
     * Returns true if given Node is accepted by NodeFilter in regard to set of
     * registered Nodes.
     */
    public boolean isAccepted(Node node) {
        return node != null ? acceptedNodes.contains(node.getClass()) : false;
    }
}
