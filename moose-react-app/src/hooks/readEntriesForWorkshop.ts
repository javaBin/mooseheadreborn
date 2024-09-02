import {EntryRegistrationForWorkshopType, ViewEntriesInputType} from "../ServerTypes";
import ServerConfig from "../ServerConfig";

const readEntriesForWorkshop =  (viewEntriesInput:ViewEntriesInputType):Promise<EntryRegistrationForWorkshopType> => {
    return new Promise((resolve, reject) => {
        fetch(ServerConfig.address + "/api/entry/workshopEntry", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(viewEntriesInput)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text().then(errorText => {
                        throw new Error(errorText);
                    });

                }
            })
            .then(json => {
                resolve(json);
            })
            .catch(error => {
                reject(error.message);
            });
    });
};

export default readEntriesForWorkshop;