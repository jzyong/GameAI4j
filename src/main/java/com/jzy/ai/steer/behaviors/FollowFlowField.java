/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.jzy.ai.steer.behaviors;

import com.game.ai.steer.Limiter;
import com.game.ai.steer.Steerable;
import com.game.ai.steer.SteeringAcceleration;
import com.game.ai.steer.SteeringBehavior;
import com.game.engine.math.Vector;

/** The {@code FollowFlowField} behavior produces a linear acceleration that tries to align the motion of the owner with the local
 * tangent of a flow field. The flow field defines a mapping from a location in space to a flow vector. Any flow field can be used
 * as the basis of this steering behavior, although it is sensitive to discontinuities in the field.
 * <p>
 * For instance, flow fields can be used for simulating various effects, such as magnetic fields, an irregular gust of wind or the
 * meandering path of a river. They can be generated by a simple random algorithm, a Perlin noise or a complicated image
 * processing. And of course flow fields can be dynamic. The only limit is your imagination.
 * <p>
 * Like {@link FollowPath}, this behavior can work in a predictive manner when its {@code predictionTime} is greater than 0.
 * 
 * @param <T> Type of vector, either 2D or 3D, implementing the {@link Vector} interface
 * 
 * @author davebaol */
public class FollowFlowField<T extends Vector<T>> extends SteeringBehavior<T> {

	/** The flow field to follow. */
	protected FlowField<T> flowField;

	/** The time in the future to predict the owner's position. Set it to 0 for non-predictive flow field following. */
	protected float predictionTime;

	/** Creates a non-predictive {@code FollowFlowField} for the specified owner.
	 * @param owner the owner of this behavior */
	public FollowFlowField(Steerable<T> owner) {
		this(owner, null);
	}

	/** Creates a non-predictive {@code FollowFlowField} for the specified owner and flow field. Prediction time defaults to 0.
	 * @param owner the owner of this behavior
	 * @param flowField the flow field to follow */
	public FollowFlowField(Steerable<T> owner, FlowField<T> flowField) {
		this(owner, flowField, 0);
	}

	/** Creates a {@code FollowFlowField} with the specified owner, flow field and prediction time.
	 * @param owner the owner of this behavior
	 * @param flowField the flow field to follow
	 * @param predictionTime the time in the future to predict the owner's position. Can be 0 for non-predictive flow field
	 *           following. */
	public FollowFlowField(Steerable<T> owner, FlowField<T> flowField, float predictionTime) {
		super(owner);
		this.flowField = flowField;
		this.predictionTime = predictionTime;
	}

	@Override
	protected SteeringAcceleration<T> calculateRealSteering (SteeringAcceleration<T> steering) {
		// Predictive or non-predictive behavior?
		T location = (predictionTime == 0) ?
		// Use the current position of the owner
		owner.getPosition()
			:
			// Calculate the predicted future position of the owner. We're reusing steering.linear here.
			steering.linear.set(owner.getPosition()).mulAdd(owner.getLinearVelocity(), predictionTime);

		// Retrieve the flow vector at the specified location
		T flowVector = flowField.lookup(location);
		
		// Clear both linear and angular components
		steering.setZero();

		if (flowVector != null && !flowVector.isZero()) {
			Limiter actualLimiter = getActualLimiter();

			// Calculate linear acceleration
			steering.linear.mulAdd(flowVector, actualLimiter.getMaxLinearSpeed()).sub(owner.getLinearVelocity())
				.limit(actualLimiter.getMaxLinearAcceleration());
		}

		// Output steering
		return steering;
	}

	/** Returns the flow field of this behavior */
	public FlowField<T> getFlowField () {
		return flowField;
	}

	/** Sets the flow field of this behavior
	 * @param flowField the flow field to set
	 * @return this behavior for chaining */
	public FollowFlowField<T> setFlowField (FlowField<T> flowField) {
		this.flowField = flowField;
		return this;
	}

	/** Returns the prediction time. */
	public float getPredictionTime () {
		return predictionTime;
	}

	/** Sets the prediction time. Set it to 0 for non-predictive flow field following.
	 * @param predictionTime the predictionTime to set
	 * @return this behavior for chaining. */
	public FollowFlowField<T> setPredictionTime (float predictionTime) {
		this.predictionTime = predictionTime;
		return this;
	}

	//
	// Setters overridden in order to fix the correct return type for chaining
	//

	@Override
	public FollowFlowField<T> setOwner (Steerable<T> owner) {
		this.owner = owner;
		return this;
	}

	@Override
	public FollowFlowField<T> setEnabled (boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	/** Sets the limiter of this steering behavior. The given limiter must at least take care of the maximum linear speed and
	 * acceleration.
	 * @return this behavior for chaining. */
	@Override
	public FollowFlowField<T> setLimiter (Limiter limiter) {
		this.limiter = limiter;
		return this;
	}

	/** A {@code FlowField} defines a mapping from a location in space to a flow vector. Typically flow fields are implemented as a
	 * multidimensional array representing a grid of cells. In each cell of the grid lives a flow vector.
	 * 
	 * @param <T> Type of vector, either 2D or 3D, implementing the {@link Vector} interface
	 * 
	 * @author davebaol */
	public interface FlowField<T extends Vector<T>> {
		/** Returns the flow vector for the specified position in space.
		 * @param position the position to map */
		public T lookup(T position);
	}
}
