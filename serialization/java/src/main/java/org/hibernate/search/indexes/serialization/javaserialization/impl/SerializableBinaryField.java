/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.hibernate.search.indexes.serialization.javaserialization.impl;

import org.apache.lucene.util.BytesRef;
import org.hibernate.search.indexes.serialization.spi.LuceneFieldContext;

/**
 * @author Emmanuel Bernard <emmanuel@hibernate.org>
 */
public class SerializableBinaryField extends SerializableField {

	private byte[] value;

	public SerializableBinaryField(LuceneFieldContext context) {
		super( context );
		final BytesRef binaryValue = context.getBinaryValue();
		final int length = binaryValue.length;
		byte[] extractedBuffer = new byte[length];
		System.arraycopy( binaryValue.bytes, binaryValue.offset, extractedBuffer, 0	, length );
		this.value = extractedBuffer;
	}

	public byte[] getValue() {
		return value;
	}

	public int getOffset() {
		return 0;
	}

	public int getLength() {
		return value.length;
	}
}
