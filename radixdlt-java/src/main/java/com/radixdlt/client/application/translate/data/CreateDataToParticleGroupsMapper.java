/*
 * (C) Copyright 2020 Radix DLT Ltd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.radixdlt.client.application.translate.data;

import java.util.Collections;
import java.util.List;

import com.radixdlt.client.application.translate.StatelessActionToParticleGroupsMapper;
import com.radixdlt.client.atommodel.cru.CRUDataParticle;
import com.radixdlt.client.atommodel.rri.RRIParticle;
import com.radixdlt.client.core.atoms.ParticleGroup;
import com.radixdlt.client.core.atoms.particles.SpunParticle;

/**
 * Maps a create CRU data action to the particles necessary to be included in an atom.
 */
public class CreateDataToParticleGroupsMapper implements StatelessActionToParticleGroupsMapper<CreateCRUDataAction> {

	private static final int INITIAL_VERSION = 0;

	/**
	 * Create {@link ParticleGroup} objects representing the specified user action.
	 *
	 * @param action the action to mapToParticles to particles
	 * @return observable of particle groups to be included in an atom for a given action
	 */
	@Override
	public List<ParticleGroup> mapToParticleGroups(CreateCRUDataAction action) {
		RRIParticle rriParticle = new RRIParticle(action.getRRI());
		CRUDataParticle cruParticule = new CRUDataParticle(action.getRRI(), INITIAL_VERSION, action.getData());
		ParticleGroup particleGroup = ParticleGroup.of(SpunParticle.down(rriParticle), SpunParticle.up(cruParticule));
		return Collections.singletonList(particleGroup);
	}
}
