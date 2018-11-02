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

one sig GroupSize{
	size: one Int
}
{size = 2}

sig Time{
	time: one Int
}
{time >= 0 and time <= 5}

-- Current position of the individual
sig Position{
	lat: one Int, -- y
	lon: one Int -- x
}
{ lat >= 0 and lat <= 5 and lon >= 0 and lon <= 5}

sig Age{
	age: one Int
}
{age >= 0 and age <= 5}

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
	age: one Age,
	position: one Position,
	data: set Data
}
{ #data > 0 and #data <= 3}

fact noDataWithoutInvididual{
	no d : Data | (all ind : Individual | d not in ind.data)
}

fact dataHasOnlyOneIndividual{
	no disj ind1, ind2 : Individual | all d1 : Data | d1 in ind1.data implies all d2 : Data | d2 in ind2.data implies d1 != d2
}

-- Batches of data refer to a specific and unique range of time
fact noOverlappingSequenceOfDataForOneIndividual{
	all ind : Individual | no disj d1, d2 : Data | d1 in ind.data and d2 in ind.data and d1.startTime.time <= d2.startTime.time and d1.endTime.time >= d2.startTime.time
}

---------------------------------------------------------------------------------------------------------------------------------------------------

--DATA4HELP

sig ThirdParty{
	dataCollectors: set DataCollector
}
-- One DataCollector for individual
{no disj d1, d2 : dataCollectors | d1 in dataCollectors and d2 in dataCollectors and d1.individual = d2.individual}

sig DataCollector{
	individual: one Individual,
	data: set Data
}
-- DataCollector can be or empty or filled with all the data owned by individual, we assume in this case that the passage of each Data is istantaneous
{ #data = 0 or data = individual.data}

sig IndividualRequest{
	thirdParty: one ThirdParty,
	individual: one Individual,
	approved: one Bool
}

sig AnonymousRequest{
	thirdParty: one ThirdParty,
	ageRange: lone AgeRange,
	zone: lone Zone,
	approved: one Bool,
	--response: lone AnonymousResponse
}
-- There is no response only in the case the request is not approved and it's approved when the anonymous group is enough big.
{/*(response = none iff approved = False) and */(approved = True iff some g: Group | g.ageRange = ageRange and g.zone = zone and #g.individuals >= GroupSize.size)}

-- For semplicity we decided to not check if this response contains the correct data
/*sig AnonymousResponse{
	data: set Data
}*/

sig AgeRange{
	startAge: one Age,
	endAge: one Age
}

sig Group{
	ageRange: lone AgeRange,
	zone: lone Zone,
	individuals: set Individual
}
-- All individual inside individuals have got the correct charateristics to be inside this group and there is no individual with these charateristics who is not inside the group
{(all ind: Individual | ind in individuals implies checkPositionInsideZone[zone, ind.position] = True and checkAgeInsideRange[ageRange, ind.age] = True) and (all ind: Individual | checkAgeInsideRange[ageRange, ind.age] = True and  checkPositionInsideZone[zone, ind.position] = True implies ind in individuals)}

-- 12
-- 34
sig Zone{
	position1: one Position, 
	position2: one Position,
	position3: one Position,
	position4: one Position,
}
{ position1.lat = position2.lat and position3.lat = position4.lat and position1.lon = position3.lon and position2.lon = position4.lon}

-- To do
fun checkPositionInsideZone[z: Zone, p: Position] : one Bool{
    	True --(z = none or not(p.lat > z.position2.lat or p.lat < z.position3.lat or p.lon < z.position3.lon or p.lon > z.position2.lon))
}

-- To do
fun checkAgeInsideRange[r: AgeRange, a: Age] : one Bool{
	True--	r = none or not (a.age < r.startAge.age or a.age > r.endAge.age)
}


--The third party can access the data only if the request has been approved by the system
fact accessDataIfAndOnlyIfConsensusIsGiven{
	all r : IndividualRequest | r.approved = False iff all d : DataCollector | d in r.thirdParty.dataCollectors implies #d.data = 0
}


---------------------------------------------------------------------------------------------------------------------------------------------------

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

-- Test
pred show{
	#Individual = 2
	#Runner = 1
	#Runner.runs = 2
	#ThirdParty = 1
	#IndividualRequest = 1
	#Data = 3
	#AnonymousRequest = 1
	#Group = 1
	#Run = 4
	AnonymousRequest.approved = True
	AnonymousRequest.zone != none
	--#Data = 5
}

run show for 1 but 6 Data, 10 Time, 2 Individual, 4 Value, 1 ThirdParty, 4 Run, 1 Runner
check fastAmbulanceCall for 1 but 6 Data, 10 Time, 2 Individual, 4 Value
---------------------------------------------------------------------------------------------------------------------------------------------------

--TRACK4RUN

sig Run{
	startTime: one Time,
	endTime: one Time
}
{startTime.time < endTime.time}

sig Runner extends Individual{
	runs: set Run
}

-- The runner cannot partecipate to different runs which are hold in the same time
fact noOverlappingRunForRuner{
	all runner : Runner | no disj r1, r2 : Run | r1 in runner.runs and r2 in runner.runs and r1.startTime.time <= r2.startTime.time and r1.endTime.time >= r2.startTime.time
}

