import { MagnifyingGlassIcon } from "@heroicons/react/24/outline";
//import { useState } from "react";

const Search = ({searchHandler}) => {

    const handleSearch = (e) => {
        e.preventDefault();

    };

    return (
        <div className="flex-grow relative">
        <form
            onSubmit={(e) => handleSearch(e)}
            className="bg-amazon-orange flex flex-grow items-center rounded-md max-md:mx-4 max-md:mb-2"
        >
            <select
                defaultValue="All"
                className="hidden md:inline h-11 w-16 rounded-l text-gray-700 px-2 text-sm bg-gray-100 border-r border-gray-300 cursor-pointer outline-none"
            >
                <option disabled value="All">
                    All
                </option>
                <option value="Computers">Computers</option>
                <option value="Arts & Crafts">Arts & Crafts</option>
                <option value="Baby">Baby</option>
                <option value="Book">Book</option>
            </select>
            <input
                // onClick={() => setShowSearch((prev) => !prev)}
                type="text"
                className="outline-none w-full h-11 text-black pl-3 max-md:rounded-l"
                placeholder="Search Amazon"
                // onChange={(ey) => setQuery(e.target.value)}
                // defaultValue={query}
            />
            <button type="submit">
                <MagnifyingGlassIcon className="text-amazon-blue_dark h-8 w-8 my-1 mx-2 cursor-pointer" />
            </button>
        </form>
       </div>
    );

}

export default Search;