import { render, screen,act } from "@testing-library/react";
import user from "@testing-library/user-event";
import RegisterParticipant from "./RegisterParticipant";



test("Should call callback on correct input",() => {
    const callbackMock = jest.fn();
    const callbackPromise = new Promise((resolve, reject) => {
        resolve(null);
    });
    callbackMock.mockReturnValue(callbackPromise);
    render(<RegisterParticipant onRegisterParticipant={callbackMock}/>)
    const nameInput = screen.getByPlaceholderText("Your name");
    const emailInput = screen.getByPlaceholderText("Enter email");
    user.type(nameInput, "John Doe");
    user.type(emailInput, "john.doe@example.com");
    const submitButton = screen.getByRole("button", {name: "Submit"});
    user.click(submitButton);

    const addParticipantInput = callbackMock.mock.calls[0][0];
    expect(addParticipantInput).toEqual({name: "John Doe",email:"john.doe@example.com"})
});

test('Should give error if submitting without email',async () => {
    const callbackMock = jest.fn();

    render(<RegisterParticipant onRegisterParticipant={callbackMock}/>);

    const alertBefore = screen.queryByRole("alert");
    expect(alertBefore).toBeNull();

    const nameInput = screen.getByPlaceholderText("Your name");
    await user.type(nameInput, "John Doe");
    const submitButton = screen.getByRole("button", {name: "Submit"});

    act(() => {
        user.click(submitButton);
    });
    //await user.click(submitButton);


    //screen.logTestingPlaygroundURL()
    //screen.debug();

    const alertElement = await screen.findByRole('alert');
    expect(alertElement).toBeInTheDocument();
    expect(alertElement).toHaveTextContent("Email is required");
});

test('Should give error if receive error from callback',async () =>  {
    const callbackMock = jest.fn();
    const callbackPromise = new Promise((resolve, reject) => {
        resolve("Error from server");
    });
    callbackMock.mockReturnValue(callbackPromise);
    render(<RegisterParticipant onRegisterParticipant={callbackMock}/>)
    const nameInput = screen.getByPlaceholderText("Your name");
    const emailInput = screen.getByPlaceholderText("Enter email");
    user.type(nameInput, "John Doe");
    user.type(emailInput, "john.doe@example.com");
    const submitButton = screen.getByRole("button", {name: "Submit"});
    user.click(submitButton);

    const alertElement = await screen.findByRole('alert');
    expect(alertElement).toBeInTheDocument();
    expect(alertElement).toHaveTextContent("Error from server");

});

