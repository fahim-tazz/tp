package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ENDTIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STARTTIME;

import java.time.LocalDateTime;
import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.Lesson;
import seedu.address.model.student.NamePredicate;
import seedu.address.model.student.Student;
import seedu.address.model.student.exceptions.ConflictingLessonsException;

/**
 * Adds an assignment to a student.
 */
public class CreateLessonCommand extends Command {

    public static final String COMMAND_WORD = "new-lesson";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a lesson to a student.\n"
        + "Parameters: "
        + PREFIX_NAME + "STUDENT_NAME "
        + PREFIX_LESSON + "LESSON_NAME "
        + PREFIX_STARTTIME + "Start time "
        + PREFIX_ENDTIME + "End time\n"
        + "Example: " + COMMAND_WORD + " "
        + PREFIX_NAME + "John Doe "
        + PREFIX_LESSON + "Math Lesson "
        + PREFIX_STARTTIME + "2023-05-21 12:00 "
        + PREFIX_ENDTIME + "2023-05-21 14:00";

    public static final String MESSAGE_DATE = "endTime must be after startTime, both in the format YYYY-MM-DD HH:mm";

    private final String lessonName;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final NamePredicate predicate;
    private final List<String> names;

    /**
     * Creates a CreateHomeworkCommand to add the specified assignment to the specified student.
     */
    public CreateLessonCommand(List<String> names, NamePredicate predicate, String lessonName, LocalDateTime startTime,
                               LocalDateTime endTime) {
        requireNonNull(lessonName);
        requireNonNull(startTime);
        requireNonNull(endTime);
        requireNonNull(predicate);

        this.lessonName = lessonName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.predicate = predicate;
        this.names = names;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        StringBuilder dupNames = new StringBuilder();
        for (String name : names) {
            if (model.hasDuplicateName(name)) {
                dupNames.append(name).append(", ");
            }
            if (dupNames.length() != 0) {
                dupNames = new StringBuilder(dupNames.substring(0, dupNames.length() - 2));
                throw new CommandException(String.format(Messages.MESSAGE_HAS_DUPLICATE_NAMES, dupNames));
            }
        }
        model.updateFilteredStudentList(predicate);

        List<Student> studentList = model.getFilteredStudentList();

        Lesson lesson = new Lesson(lessonName, startTime, endTime);

        try {
            for (Student student : studentList) {
                student.addLesson(lesson);
            }
        } catch (ConflictingLessonsException e) {
            throw new CommandException(e.getMessage());
        }

        StringBuilder sb = new StringBuilder();
        for (Student student : studentList) {
            sb.append(student.getName().fullName);
            sb.append("\n");
        }

        return new CommandResult(
            String.format(Messages.MESSAGE_LESSON_ADDED_SUCCESS, lesson, sb));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
            || (other instanceof CreateLessonCommand // instanceof handles nulls
            && predicate.equals(((CreateLessonCommand) other).predicate)
            && lessonName.equals(((CreateLessonCommand) other).lessonName)
            && startTime.equals(((CreateLessonCommand) other).startTime)
            && endTime.equals(((CreateLessonCommand) other).endTime));
    }
}

