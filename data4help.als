-- Let's assume that data of individuals, time, latitude and longitude are integers.
open util/integer as integer
open util/boolean as boolean

one sig Threshold{
	threshold: one Int
}
{threshold = 4}

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
{ #data >= 0 and #data <= 3}

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
{data = individual.data and no disj t1, t2: ThirdParty | this in t1.dataCollectors and this in t2.dataCollectors}
-- DataCollector can be or empty or filled with all the data owned by individual, we assume in this case that the passage of each Data is istantaneous

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
{(all ind: Individual | ind in individuals implies checkPositionInsideZone[zone, ind.position] and checkAgeInsideRange[ageRange, ind.age]) and (all ind: Individual | checkAgeInsideRange[ageRange, ind.age] and  checkPositionInsideZone[zone, ind.position] implies ind in individuals)}

-- 12
-- 34
sig Zone{
	position1: one Position, 
	position2: one Position,
	position3: one Position,
	position4: one Position,
}
{ position1.lat = position2.lat and position3.lat = position4.lat and position1.lon = position3.lon and position2.lon = position4.lon}

pred checkPositionInsideZone[z: Zone, p: Position]{
    	not(p.lat > z.position2.lat or p.lat < z.position3.lat or p.lon < z.position3.lon or p.lon > z.position2.lon)
}

pred checkAgeInsideRange[r: AgeRange, a: Age]{
	not (a.age < r.startAge.age or a.age > r.endAge.age)
}

fact noDataCollectorWithoutThirdParty{
	no d : DataCollector | all t : ThirdParty | d not in t.dataCollectors
}

--The third party can access the data only if the request has been approved by the system
fact accessDataIfAndOnlyIfConsensusIsGiven{
	all r : IndividualRequest | r.approved = False iff no d : DataCollector | d in r.thirdParty.dataCollectors and d.individual = r.individual
}

pred show1{
	AnonymousRequest.approved = True
}
pred show2{
	AnonymousRequest.approved = False
}
pred show3{
	IndividualRequest.approved = True
}
pred show4{
	IndividualRequest.approved = False
}
run show1 for 3 but 1 AnonymousRequest, 0 IndividualRequest, 2 Individual, 0 DataCollector, 1 ThirdParty, 1 Zone, 1 AgeRange, 1 Group, 0 Value, 0 Data, 0 Time
run show2 for 3 but 1 AnonymousRequest, 0 IndividualRequest, 2 Individual, 0 DataCollector, 1 ThirdParty, 1 Zone, 1 AgeRange, 1 Group, 0 Value, 0 Data, 0 Time
run show3 for 2 but 0 AnonymousRequest, 0 Zone, 0 AgeRange, 1 IndividualRequest, 1 Individual, 1 Age, 1 Position, 1 DataCollector
run show4 for 2 but 0 AnonymousRequest, 0 Zone, 0 AgeRange, 1 IndividualRequest, 1 Individual, 1 Age, 1 Position, 1 DataCollector
