module Main

include("FileParser.jl")

function main()
    # Read the given problem
    problemdescription = FileParser.parseFile("../test_data/1.txt")

    #
end

main()
end 