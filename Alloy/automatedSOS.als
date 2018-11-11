-- Let's assume that data of individuals, time, latitude and longitude are integers.
open util/integer as integer
open util/boolean as boolean

one sig Threshold{
	threshold: one Int
}
{threshold = 4}

one sig ReactionTime{
	time: one Int
}
{time = 3}

sig Time{
	time: one Int
}
{time >= 0 and time <= 5}

-- Atomic piece of data
sig Value{
	value: one Int
}
{value >=0 and value <=5}

-- Batch of data of some individual during a fixed range of time
sig Data{
	startTime: one Time,
	endTime: one Time,
	-- Array of values
	values: seq Value,
	-- True if and only if exists a value in values that is higher than the threshold
	danger: one Bool
}
-- Maximum batch slot duration is 2 unit of time
{startTime.time < endTime.time and #values > 0 and #values <= 5 and sub[endTime.time, startTime.time] <= 2 and (danger = True iff some i : values.inds | values[i].value > Threshold.threshold)}

sig Individual{
	--age: one Age,
	--position: one Position,
	data: set Data
}
{ #data > 0 and #data <= 3}

fact noDataWithoutInvididual{
	no d : Data | (all ind : Individual | d not in ind.data)
}

fact dataHasOnlyOneIndividual{
	no d : Data | some disj ind1, ind2 : Individual | d in ind1.data and d in ind2.data
}

-- Batches of data refer to a specific and unique range of time
fact noOverlappingSequenceOfDataForOneIndividual{
	all ind : Individual | no disj d1, d2 : Data | d1 in ind.data and d2 in ind.data and d1.startTime.time <= d2.startTime.time and d1.endTime.time >= d2.startTime.time
}

--AUTOMATEDSOS

sig AmbulanceCall{
	individual: one Individual,
	time: one Time
}

-- We consider only the case in which just an ambulance is sent for individual
fact atMostOnlyOneAmbulance{
	no disj c1, c2 : AmbulanceCall | c1.individual = c2.individual 
}

-- The ambulance is called if and only if there is a value which is higher than the threshold. We assume 1 unit of time is needed to process the data
fact callAmbulanceIfAndOnlyIfNeeded{
	(all c : AmbulanceCall | some d : Data | d in c.individual.data and d.danger = True and d.startTime.time < c.time.time) and (all ind : Individual | all d : Data | d in ind.data and d.danger = True implies some c : AmbulanceCall | c.individual = ind and c.time.time > d.endTime.time and sub[c.time.time, d.endTime.time] <= 1 )
}

-- Check if the call of the Ambulance is done within the reaction time (this is assured by the boundaries on the duration of the batch slots and on the time to process the data)
assert fastAmbulanceCall{
	--all t : Time | t.time = 0
	all ind : Individual | all d : Data | d in ind.data and d.danger = True implies some c : AmbulanceCall | c.individual = ind and sub[c.time.time, d.startTime.time] <= ReactionTime.time
	
}

pred show{}
run show for 3 but 2 Individual, 1 AmbulanceCall
check fastAmbulanceCall for 1 but 6 Data, 10 Time, 2 Individual, 4 Value
---------------------------------------------------------------------------------------------------------------------------------------------------


