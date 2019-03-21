module FileParser
export FileParser

mutable struct ProblemDescription
    filename::String
    n::Int
    m::Int
    machines
    time
end

ProblemDescription() =  ProblemDescription("",0,0,[],[])


function parseFile(filename)
    problemdescription = ProblemDescription()
    problemdescription.filename = filename
    open(filename) do file
        for (i,line) in enumerate(eachline(file))
            if i == 1
                line = [parse(Int, ss) for ss in split(line)]
                problemdescription.n = line[1]
                problemdescription.m = line[2]
                println(problemdescription.n)
                println(problemdescription.m)
            else
                line = [parse(Int,ss) for ss in split(line)]
                push!(problemdescription.machines,line[1:2:length(line)-1])
                push!(problemdescription.time,line[2:2:length(line)])
            end 
        end 
    end
    println(problemdescription)
end

end
