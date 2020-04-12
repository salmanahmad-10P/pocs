package org.acme.constraints;

import org.acme.domain.Lesson;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;


// By implementing ConstraintProvider, evaluation is done incrementally
public class SchoolSchedulingConstraints implements ConstraintProvider{

	@Override
	public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
		return new Constraint[] {
            //roomConflict(constraintFactory)

        };
    }
    
    private Constraint roomConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
            .from(Lesson.class) // evaluate based on all Lessons
            .join(Lesson.class, 
                    Joiners.equal(Lesson::getRoom),
                    Joiners.equal(Lesson::getTimeSlot)) // filter for 2 lessons in same room at the same time
            .penalize("Room conflict", HardSoftScore.ONE_HARD); // if there are 2 lessons that meet above criteria, penalize the parent schedule 
    }

}