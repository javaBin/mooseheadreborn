import {EntryRegistrationForWorkshopType, ReadEntryRegistrationInputType} from "../ServerTypes";
import ServerConfig from "../ServerConfig";

const readCheckinFromServer = (readEntryRegistrationInput:ReadEntryRegistrationInputType):Promise<EntryRegistrationForWorkshopType> => {
    return new Promise((resolve, reject) => {

        fetch(ServerConfig.address + "/api/admin/readCheckin", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(readEntryRegistrationInput)
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
    })
};

export default readCheckinFromServer;