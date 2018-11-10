-- Let's assume that data of individuals, time, latitude and longitude are integers.
open util/integer as integer
--open util/boolean as boolean

sig Time{
	time: one Int
}
{time >= 0 and time <= 5}

--TRACK4RUN

sig Run{
	startTime: one Time,
	endTime: one Time,
	runners: set Runner,
	maxSize: one Int
}
{startTime.time < endTime.time and #runners <=  maxSize and maxSize <= 5 and no disj r1, r2 : Runner | r1 in runners and r2 in runners and r1 = r2}

sig Runner{
	runs: set Run
}
{no disj r1, r2 : Run | r1 in runs and r2 in runs and r1 = r2}

fact runnerInRunIfRunnerInRun{
	all race : Run | all r : Runner | r in race.runners implies race in r.runs
}

fact runInRunnerIfRunInRunner{
	all r : Runner | all race : Run | race in r.runs implies r in race.runners
}

-- The runner cannot partecipate to different runs which are hold in the same time
fact noOverlappingRunForRunner{
	all runner : Runner | no disj r1, r2 : Run | r1 in runner.runs and r2 in runner.runs and r1.startTime.time <= r2.startTime.time and r1.endTime.time >= r2.startTime.time
}

pred show[r1: Runner, r2: Runner, race1: Run, race2: Run]{
	r1 != r2 and race1 != race2
	addRunnerToRun[r1, race1]
	addRunnerToRun[r2, race1]
	addRunnerToRun[r1, race2]
}

pred addRunnerToRun[r: Runner, race: Run]{
	r in race.runners
}

run show for 4 but 2 Run, 2 Runner

